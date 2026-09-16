package br.com.inova.sigin.delivery.pedido.service;

import br.com.inova.sigin.delivery.evento.service.EventoProducaoService;
import br.com.inova.sigin.delivery.pedido.dto.PedidoPendenciaRequest;
import br.com.inova.sigin.delivery.pedido.dto.PedidoResponse;
import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import br.com.inova.sigin.delivery.pedido.mapper.PedidoMapper;
import br.com.inova.sigin.delivery.pedido.repository.PedidoRepository;
import br.com.inova.sigin.delivery.pedidohistorico.service.PedidoHistoricoService;
import br.com.inova.sigin.delivery.pedidoitem.entity.PedidoItem;
import br.com.inova.sigin.delivery.pedidoitem.enums.StatusOperacao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoItemOperacaoService {

    private final PedidoRepository repository;
    private final PedidoMapper mapper;
    private final PedidoHistoricoService historicoService;
    private final EventoProducaoService eventoProducaoService;

    @Transactional
    public PedidoResponse iniciarProducaoItem(Long pedidoId, Long itemId) {
        Pedido pedido = buscarEntidade(pedidoId);
        PedidoItem item = buscarItemDoPedido(pedido, itemId);

        validarItemNaoCancelado(item);
        validarItemDeProducao(item);

        if (item.getStatusOperacao() != StatusOperacao.APROVADO) {
            throw new IllegalArgumentException(
                    "Item não pode iniciar produção a partir do status atual."
            );
        }

        item.setStatusOperacao(StatusOperacao.EM_PRODUCAO);
        pedido.setStatusAlteradoEm(LocalDateTime.now());

        historicoService.registrar(
                pedido,
                null,
                "Sistema",
                getSetor(item),
                "PRODUCAO_INICIADA",
                "Produção iniciada."
        );

        repository.save(pedido);

        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse colocarPendenteItem(
            Long pedidoId,
            Long itemId,
            PedidoPendenciaRequest request
    ) {
        Pedido pedido = buscarEntidade(pedidoId);
        PedidoItem item = buscarItemDoPedido(pedido, itemId);

        validarItemNaoCancelado(item);
        validarItemDeProducao(item);

        if (item.getStatusOperacao() != StatusOperacao.APROVADO) {
            throw new IllegalArgumentException(
                    "Item não pode ser colocado em espera a partir do status atual."
            );
        }

        item.setStatusOperacao(StatusOperacao.PENDENTE);
        pedido.setObservacaoOperacao(request.getMotivo());
        pedido.setStatusAlteradoEm(LocalDateTime.now());

        repository.save(pedido);

        historicoService.registrar(
                pedido,
                null,
                "Sistema",
                getSetor(item),
                "PENDENTE",
                request.getMotivo()
        );

        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse finalizarItem(Long pedidoId, Long itemId) {
        Pedido pedido = buscarEntidade(pedidoId);
        PedidoItem item = buscarItemDoPedido(pedido, itemId);

        validarItemNaoCancelado(item);
        validarItemDeProducao(item);

        if (item.getStatusOperacao() != StatusOperacao.EM_PRODUCAO) {
            throw new IllegalArgumentException(
                    "Item não pode ser finalizado a partir do status atual."
            );
        }

        item.setStatusOperacao(StatusOperacao.FINALIZADO);

        boolean todosFinalizados = pedido.getItens()
                .stream()
                .filter(this::ehItemProducao)
                .allMatch(outroItem ->
                        outroItem.getStatusOperacao() == StatusOperacao.FINALIZADO
                                || outroItem.getStatusOperacao() == StatusOperacao.CANCELADO
                );

        if (todosFinalizados) {
            pedido.setStatus(StatusPedido.AGUARDANDO_SEPARACAO);
        }

        pedido.setStatusAlteradoEm(LocalDateTime.now());
        repository.save(pedido);

        historicoService.registrar(
                pedido,
                null,
                "Sistema",
                getSetor(item),
                "FINALIZADO",
                "Item finalizou a produção."
        );

        boolean pedidoFicouProntoParaSeparacao =
                pedido.getStatus() == StatusPedido.AGUARDANDO_SEPARACAO;

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        eventoProducaoService.pedidoItemFinalizado(
                                pedido,
                                item
                        );

                        if (pedidoFicouProntoParaSeparacao) {
                            eventoProducaoService.pedidoPronto(
                                    pedido,
                                    getSetor(item)
                            );
                        }
                    }
                }
        );

        return mapper.toResponse(pedido);
    }

    /**
     * Valida se os itens do pedido estão aptos para a finalização da venda.
     * <p>
     * Regras:
     * - Itens BALCAO não exigem produção e podem ser concluídos automaticamente.
     * - Itens COZINHA/PIZZARIA somente podem prosseguir se estiverem
     * FINALIZADOS ou CANCELADOS.
     * - Nenhum item de produção é finalizado artificialmente.
     */
    @Transactional
    public void validarEConcluirParaFaturamento(Pedido pedido) {
        List<String> bloqueios = new ArrayList<>();
        boolean alterou = false;

        for (PedidoItem item : pedido.getItens()) {
            StatusOperacao status = item.getStatusOperacao();

            if (status == null) {
                bloqueios.add(
                        formatarBloqueio(
                                item,
                                "sem status operacional definido"
                        )
                );
                continue;
            }

            if (status == StatusOperacao.CANCELADO
                    || status == StatusOperacao.FINALIZADO) {
                continue;
            }

            if (ehItemProducao(item)) {
                bloqueios.add(
                        formatarBloqueio(
                                item,
                                "aguarda conclusão da produção"
                        )
                );
                continue;
            }

            /*
             * Item que não exige produção, especialmente BALCAO.
             *
             * A venda pode ser finalizada sem passar por produção.
             */
            item.setStatusOperacao(StatusOperacao.FINALIZADO);
            alterou = true;

            historicoService.registrar(
                    pedido,
                    null,
                    "Sistema",
                    getSetor(item),
                    "FINALIZADO",
                    "Item concluído automaticamente no fechamento da venda."
            );
        }

        if (!bloqueios.isEmpty()) {
            throw new IllegalArgumentException(
                    "Venda não pode ser finalizada. "
                            + String.join("; ", bloqueios)
            );
        }

        if (alterou) {
            pedido.setStatusAlteradoEm(LocalDateTime.now());
            repository.save(pedido);
        }
    }

    private Pedido buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Pedido não encontrado."
                        )
                );
    }

    private PedidoItem buscarItemDoPedido(
            Pedido pedido,
            Long itemId
    ) {
        return pedido.getItens()
                .stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Item não pertence ao pedido informado."
                        )
                );
    }

    private void validarItemNaoCancelado(PedidoItem item) {
        if (item.getStatusOperacao() == StatusOperacao.CANCELADO) {
            throw new IllegalArgumentException(
                    "Item cancelado não pode ser operado."
            );
        }
    }

    private void validarItemDeProducao(PedidoItem item) {
        if (!ehItemProducao(item)) {
            throw new IllegalArgumentException(
                    "Item do Balcão não participa da produção."
            );
        }
    }

    private String getSetor(PedidoItem item) {
        return item.getSetor();
    }

    private boolean ehItemProducao(PedidoItem item) {
        String setor = item.getSetor();

        return setor != null
                && ("COZINHA".equalsIgnoreCase(setor)
                || "PIZZARIA".equalsIgnoreCase(setor));
    }

    private String formatarBloqueio(
            PedidoItem item,
            String motivo
    ) {
        String produto = item.getProdutoNome();

        if (produto == null || produto.isBlank()) {
            produto = "Item #" + item.getId();
        }

        String setor = getSetor(item);
        String status = item.getStatusOperacao() == null
                ? "SEM_STATUS"
                : item.getStatusOperacao().name();

        return produto
                + " ["
                + (setor == null ? "SEM_SETOR" : setor)
                + "]"
                + " - status "
                + status
                + ": "
                + motivo
                + ".";
    }
}
