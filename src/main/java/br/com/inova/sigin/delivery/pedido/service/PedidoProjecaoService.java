package br.com.inova.sigin.delivery.pedido.service;

import br.com.inova.sigin.delivery.core.dto.PedidoItemResponse;
import br.com.inova.sigin.delivery.core.dto.PedidoResponse;
import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import br.com.inova.sigin.delivery.pedido.mapper.PedidoMapper;
import br.com.inova.sigin.delivery.pedido.repository.PedidoRepository;
import br.com.inova.sigin.delivery.pedidoitem.entity.PedidoItem;
import br.com.inova.sigin.delivery.pedidoitem.enums.StatusOperacao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PedidoProjecaoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;

    @Transactional
    public br.com.inova.sigin.delivery.pedido.dto.PedidoResponse projetar(
            PedidoResponse coreResponse,
            String clienteWhatsapp
    ) {

        if (coreResponse == null || coreResponse.id() == null) {
            throw new IllegalArgumentException(
                    "Resposta do SIGIN Core inválida: pedido sem ID."
            );
        }

        Pedido existente = pedidoRepository
                .findByCorePedidoId(coreResponse.id())
                .orElse(null);

        if (existente != null) {
            sincronizarPedido(existente, coreResponse, clienteWhatsapp);

            return pedidoMapper.toResponse(
                    pedidoRepository.save(existente)
            );
        }

        if (coreResponse.itens() == null || coreResponse.itens().isEmpty()) {
            throw new IllegalStateException(
                    "Pedido do SIGIN Core não possui itens."
            );
        }

        LocalDateTime agora = LocalDateTime.now();

        Pedido pedido = Pedido.builder()
                .corePedidoId(coreResponse.id())
                .clienteId(coreResponse.clienteId())
                .clienteNome(extrairClienteNome(coreResponse))
                .clienteWhatsapp(clienteWhatsapp)
                .tipoRecebimento(coreResponse.tipoRecebimento())
                .status(StatusPedido.RECEBIDO)
                .valorProdutos(coreResponse.valorProdutos())
                .taxaEntrega(coreResponse.taxaEntrega())
                .valorTotal(coreResponse.valorTotal())
                .observacao(coreResponse.observacao())
                .dataCriacao(
                        coreResponse.dataPedido() != null
                                ? coreResponse.dataPedido()
                                : agora
                )
                .statusAlteradoEm(agora)
                .build();

        for (PedidoItemResponse itemResponse : coreResponse.itens()) {
            pedido.getItens().add(
                    projetarItem(itemResponse, pedido)
            );
        }

        return pedidoMapper.toResponse(
                pedidoRepository.save(pedido)
        );
    }

    private void sincronizarPedido(
            Pedido pedido,
            PedidoResponse coreResponse,
            String clienteWhatsapp
    ) {

        pedido.setClienteId(coreResponse.clienteId());
        pedido.setClienteNome(extrairClienteNome(coreResponse));

        if (clienteWhatsapp != null) {
            pedido.setClienteWhatsapp(clienteWhatsapp);
        }

        pedido.setTipoRecebimento(coreResponse.tipoRecebimento());
        pedido.setValorProdutos(coreResponse.valorProdutos());
        pedido.setTaxaEntrega(coreResponse.taxaEntrega());
        pedido.setValorTotal(coreResponse.valorTotal());
        pedido.setObservacao(coreResponse.observacao());

        sincronizarItens(pedido, coreResponse);
    }

    private void sincronizarItens(
            Pedido pedido,
            PedidoResponse coreResponse
    ) {

        if (coreResponse.itens() == null) {
            return;
        }

        Map<Long, PedidoItem> itensExistentes = new HashMap<>();

        for (PedidoItem item : pedido.getItens()) {
            if (item.getCoreItemId() != null) {
                itensExistentes.put(
                        item.getCoreItemId(),
                        item
                );
            }
        }

        Set<Long> idsRecebidosDoCore = new HashSet<>();

        for (PedidoItemResponse coreItem : coreResponse.itens()) {

            if (coreItem == null || coreItem.id() == null) {
                throw new IllegalStateException(
                        "Item do pedido do SIGIN Core sem ID."
                );
            }

            PedidoItem item = itensExistentes.get(coreItem.id());

            if (item != null) {
                atualizarDadosComerciais(item, coreItem);
            } else {
                item = projetarItem(coreItem, pedido);
                pedido.getItens().add(item);
            }

            idsRecebidosDoCore.add(coreItem.id());
        }

        pedido.getItens().removeIf(item ->
                item.getCoreItemId() != null
                        && !idsRecebidosDoCore.contains(item.getCoreItemId())
        );
    }

    private void atualizarDadosComerciais(
            PedidoItem item,
            PedidoItemResponse coreItem
    ) {

        if (coreItem.produtoId() == null) {
            throw new IllegalStateException(
                    "Item do pedido do SIGIN Core sem produtoId."
            );
        }

        item.setCoreProdutoId(coreItem.produtoId());
        item.setProdutoNome(coreItem.produto());
        item.setQuantidade(
                converterQuantidade(coreItem.quantidade())
        );
        item.setValorUnitario(coreItem.valorUnitario());
        item.setValorTotal(coreItem.valorTotal());
        item.setSetor(coreItem.setor());

        // Estado operacional permanece intacto.
    }

    private PedidoItem projetarItem(
            PedidoItemResponse coreItem,
            Pedido pedido
    ) {

        if (coreItem == null || coreItem.id() == null) {
            throw new IllegalStateException(
                    "Item do pedido do SIGIN Core sem ID."
            );
        }

        if (coreItem.produtoId() == null) {
            throw new IllegalStateException(
                    "Item do pedido do SIGIN Core sem produtoId."
            );
        }

        return PedidoItem.builder()
                .pedido(pedido)
                .coreItemId(coreItem.id())
                .coreProdutoId(coreItem.produtoId())
                .produtoNome(coreItem.produto())
                .quantidade(
                        converterQuantidade(coreItem.quantidade())
                )
                .valorUnitario(coreItem.valorUnitario())
                .valorTotal(coreItem.valorTotal())
                .setor(coreItem.setor())
                .separado(false)
                .statusOperacao(StatusOperacao.PENDENTE)
                .build();
    }

    private Integer converterQuantidade(
            BigDecimal quantidade
    ) {

        if (quantidade == null) {
            throw new IllegalStateException(
                    "Item do pedido do SIGIN Core sem quantidade."
            );
        }

        try {
            return quantidade.intValueExact();
        } catch (ArithmeticException exception) {
            throw new IllegalStateException(
                    "A quantidade "
                            + quantidade
                            + " do pedido do SIGIN Core não é compatível "
                            + "com a quantidade operacional inteira do Delivery.",
                    exception
            );
        }
    }

    private String extrairClienteNome(
            PedidoResponse coreResponse
    ) {

        if (coreResponse.cliente() == null) {
            return null;
        }

        if (coreResponse.cliente() instanceof String cliente) {
            return cliente;
        }

        return null;
    }
}