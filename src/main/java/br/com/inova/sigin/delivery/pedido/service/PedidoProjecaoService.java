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
            return pedidoMapper.toResponse(existente);
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

    private PedidoItem projetarItem(
            PedidoItemResponse coreItem,
            Pedido pedido
    ) {

        if (coreItem == null || coreItem.produtoId() == null) {
            throw new IllegalStateException(
                    "Item do pedido do SIGIN Core sem produtoId."
            );
        }

        return PedidoItem.builder()
                .pedido(pedido)
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