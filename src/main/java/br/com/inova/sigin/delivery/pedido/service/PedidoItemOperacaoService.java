package br.com.inova.sigin.delivery.pedido.service;

import br.com.inova.sigin.delivery.core.client.CoreClient;
import br.com.inova.sigin.delivery.pedido.dto.PedidoItemRequest;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PedidoItemOperacaoService {

    private final PedidoRepository repository;
    private final PedidoMapper mapper;
    private final PedidoHistoricoService historicoService;
    private final CoreClient coreClient;
    private final PedidoProjecaoService pedidoProjecaoService;

    @Transactional
    public PedidoResponse iniciarProducaoItem(Long pedidoId, Long itemId) {
        Pedido pedido = buscarEntidade(pedidoId);
        PedidoItem item = buscarItemDoPedido(pedido, itemId);

        validarItemNaoCancelado(item);

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

        if (item.getStatusOperacao() != StatusOperacao.EM_PRODUCAO) {
            throw new IllegalArgumentException(
                    "Item não pode ser finalizado a partir do status atual."
            );
        }

        item.setStatusOperacao(StatusOperacao.FINALIZADO);

        boolean todosFinalizados = pedido.getItens()
                .stream()
                .allMatch(outroItem ->
                        outroItem.getStatusOperacao() == StatusOperacao.FINALIZADO
                                || outroItem.getStatusOperacao() == StatusOperacao.CANCELADO
                );

        if (todosFinalizados) {
            pedido.setStatus(StatusPedido.FINALIZADO);
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

        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse adicionarItem(
            Long pedidoId,
            PedidoItemRequest request
    ) {
        Pedido pedido = buscarEntidade(pedidoId);

        if (request.getProdutoId() == null) {
            throw new IllegalArgumentException(
                    "Produto é obrigatório."
            );
        }

        if (request.getQuantidade() == null || request.getQuantidade() <= 0) {
            throw new IllegalArgumentException(
                    "Quantidade deve ser maior que zero."
            );
        }

        br.com.inova.sigin.delivery.core.dto.PedidoResponse coreResponse =
                coreClient.adicionarItem(
                        pedido.getCorePedidoId(),
                        request
                );

        return sincronizar(pedido, coreResponse);
    }

    @Transactional
    public PedidoResponse alterarQuantidadeItem(
            Long pedidoId,
            Long itemId,
            PedidoItemRequest request
    ) {
        Pedido pedido = buscarEntidade(pedidoId);

        PedidoItem item = buscarItemDoPedido(pedido, itemId);

        validarItemEditavel(item);

        if (request.getQuantidade() == null || request.getQuantidade() <= 0) {
            throw new IllegalArgumentException(
                    "Quantidade deve ser maior que zero."
            );
        }

        Long coreItemId = obterCoreItemId(item);

        br.com.inova.sigin.delivery.core.dto.PedidoResponse coreResponse =
                coreClient.alterarQuantidadeItem(
                        pedido.getCorePedidoId(),
                        coreItemId,
                        BigDecimal.valueOf(request.getQuantidade())
                );

        return sincronizar(pedido, coreResponse);
    }

    @Transactional
    public PedidoResponse removerItem(
            Long pedidoId,
            Long itemId
    ) {
        Pedido pedido = buscarEntidade(pedidoId);

        PedidoItem item = buscarItemDoPedido(pedido, itemId);

        validarItemEditavel(item);

        Long coreItemId = obterCoreItemId(item);

        coreClient.removerItem(
                pedido.getCorePedidoId(),
                coreItemId
        );

        br.com.inova.sigin.delivery.core.dto.PedidoResponse coreResponse =
                coreClient.buscarPedido(
                        pedido.getCorePedidoId()
                );

        return sincronizar(pedido, coreResponse);
    }

    private PedidoResponse sincronizar(
            Pedido pedido,
            br.com.inova.sigin.delivery.core.dto.PedidoResponse coreResponse
    ) {
        return pedidoProjecaoService.projetar(
                coreResponse,
                pedido.getClienteWhatsapp()
        );
    }

    private void validarItemEditavel(PedidoItem item) {
        validarItemNaoCancelado(item);

        if (item.getStatusOperacao() == StatusOperacao.EM_PRODUCAO
                || item.getStatusOperacao() == StatusOperacao.FINALIZADO) {

            throw new IllegalArgumentException(
                    "Item não pode ser alterado após o início da produção."
            );
        }
    }

    private Long obterCoreItemId(PedidoItem item) {
        if (item.getCoreItemId() == null) {
            throw new IllegalStateException(
                    "Item sem referência ao item correspondente no SIGIN Core."
            );
        }

        return item.getCoreItemId();
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

    private String getSetor(PedidoItem item) {
        return item.getSetor();
    }
}