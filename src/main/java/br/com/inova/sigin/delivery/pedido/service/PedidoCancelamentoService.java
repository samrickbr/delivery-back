package br.com.inova.sigin.delivery.pedido.service;

import br.com.inova.sigin.delivery.core.client.CoreClient;
import br.com.inova.sigin.delivery.pedido.dto.CancelamentoItensRequest;
import br.com.inova.sigin.delivery.pedido.dto.CancelamentoRequest;
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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoCancelamentoService {

    private final PedidoRepository repository;
    private final PedidoMapper mapper;
    private final PedidoHistoricoService historicoService;
    private final CoreClient coreClient;
    private final PedidoProjecaoService pedidoProjecaoService;

    @Transactional
    public PedidoResponse cancelar(Long id, String setor, CancelamentoRequest request) {
        Pedido pedido = buscarEntidade(id);
        String justificativa = validarJustificativa(request.getJustificativa());
        List<PedidoItem> itensParaCancelar = pedido.getItens()
                .stream()
                .filter(item -> getSetor(item).equals(setor))
                .filter(item -> item.getStatusOperacao() != StatusOperacao.CANCELADO)
                .toList();

        itensParaCancelar.forEach(item ->
                cancelarItemLocal(item, justificativa)
        );

        boolean todosCancelados = pedido.getItens()
                .stream()
                .allMatch(item ->
                        item.getStatusOperacao() == StatusOperacao.CANCELADO
                );

        if (todosCancelados) {
            pedido.setStatus(StatusPedido.CANCELADO);
        }

        pedido.setObservacaoOperacao(justificativa);
        pedido.setStatusAlteradoEm(LocalDateTime.now());

        removerItensNoCore(pedido, itensParaCancelar);
        PedidoResponse response = sincronizar(pedido);

        historicoService.registrar(
                pedido,
                null,
                "Sistema",
                setor,
                "SETOR_CANCELADO",
                "Todos os itens do setor foram cancelados. Motivo: " + justificativa
        );

        return response;
    }

    @Transactional
    public PedidoResponse cancelarPedido(Long id, CancelamentoRequest request) {
        return cancelarPedidoCompleto(id, request.getJustificativa());
    }

    @Transactional
    public PedidoResponse cancelarItemComercial(
            Long pedidoId,
            Long itemId,
            CancelamentoRequest request
    ) {
        Pedido pedido = buscarEntidade(pedidoId);
        PedidoItem item = buscarItemDoPedido(pedido, itemId);
        String justificativa = validarJustificativa(request.getJustificativa());

        if (item.getStatusOperacao() == StatusOperacao.CANCELADO) {
            throw new IllegalArgumentException(
                    "Item já está cancelado."
            );
        }

        cancelarItemLocal(item, justificativa);
        pedido.setStatusAlteradoEm(LocalDateTime.now());
        removerItensNoCore(pedido, List.of(item));

        PedidoResponse response = sincronizar(pedido);

        historicoService.registrar(
                pedido,
                null,
                "Sistema",
                getSetor(item),
                "ITEM_CANCELADO",
                item.getQuantidade() + "x " + item.getProdutoNome()
                        + " - Motivo: " + justificativa
        );

        return response;
    }

    @Transactional
    public PedidoResponse cancelarItens(Long id, String setor, CancelamentoItensRequest request) {
        Pedido pedido = buscarEntidade(id);
        String justificativa = validarJustificativa(request.getJustificativa());
        List<PedidoItem> itensSelecionados = request.getItens()
                .stream()
                .distinct()
                .map(itemId -> buscarItemDoPedido(pedido, itemId))
                .toList();

        for (PedidoItem item : itensSelecionados) {

            String setorItem = getSetor(item);

            if (!setorItem.equals(setor) && !setor.equals("BALCAO")) {
                throw new IllegalArgumentException(
                        "Usuário não pode cancelar este item."
                );
            }

            if (item.getStatusOperacao() == StatusOperacao.CANCELADO) {
                continue;
            }

            cancelarItemLocal(item, justificativa);

            historicoService.registrar(
                    pedido,
                    null,
                    "Sistema",
                    setor,
                    "ITEM_CANCELADO",
                    item.getQuantidade() + "x " + item.getProdutoNome() + " - Motivo: " + justificativa
            );
        }

        List<PedidoItem> itensParaCancelar = itensSelecionados
                .stream()
                .filter(item ->
                        item.getStatusOperacao() != StatusOperacao.CANCELADO
                )
                .toList();

        itensParaCancelar.forEach(item ->
                cancelarItemLocal(item, justificativa)
        );

        pedido.setStatusAlteradoEm(LocalDateTime.now());
        removerItensNoCore(pedido, itensParaCancelar);

        return sincronizar(pedido);
    }

    @Transactional
    public PedidoResponse cancelarPedidoCompleto(Long id, String justificativa) {
        Pedido pedido = buscarEntidade(id);
        String motivo = validarJustificativa(justificativa);
        List<PedidoItem> itensParaCancelar = pedido.getItens()
                .stream()
                .filter(item -> item.getStatusOperacao() != StatusOperacao.CANCELADO)
                .toList();

        itensParaCancelar.forEach(item ->
                cancelarItemLocal(item, motivo)
        );

        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.setStatusAlteradoEm(LocalDateTime.now());
        removerItensNoCore(pedido, itensParaCancelar);
        PedidoResponse response = sincronizar(pedido);

        historicoService.registrar(
                pedido,
                null,
                "Sistema",
                "BALCAO",
                "PEDIDO_CANCELADO",
                motivo
        );

        return response;
    }

    private void removerItensNoCore(
            Pedido pedido,
            List<PedidoItem> itens
    ) {
        itens.stream()
                .forEach(item -> coreClient.removerItem(
                        pedido.getCorePedidoId(),
                        obterCoreItemId(item)
                ));
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

    private PedidoResponse sincronizar(Pedido pedido) {
        return pedidoProjecaoService.projetar(
                coreClient.buscarPedido(pedido.getCorePedidoId()),
                pedido.getClienteWhatsapp()
        );
    }

    private void cancelarItemLocal(
            PedidoItem item,
            String justificativa
    ) {
        item.setStatusOperacao(StatusOperacao.CANCELADO);
        item.setMotivoCancelamento(justificativa);
        item.setCanceladoEm(LocalDateTime.now());
        item.setCanceladoPor("Sistema");
    }

    private Long obterCoreItemId(PedidoItem item) {
        if (item.getCoreItemId() == null) {
            throw new IllegalStateException(
                    "Item sem referência ao item correspondente no SIGIN Core."
            );
        }

        return item.getCoreItemId();
    }

    private String validarJustificativa(String justificativa) {
        if (justificativa == null || justificativa.isBlank()) {
            throw new IllegalArgumentException(
                    "Motivo do cancelamento é obrigatório."
            );
        }

        return justificativa.trim();
    }

    private Pedido buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow();
    }

    private String getSetor(PedidoItem item) {
        return item.getSetor();
    }
}