package br.com.inova.sigin.delivery.pedido.mapper;

import br.com.inova.sigin.delivery.pedido.dto.ItemOperacaoResponse;
import br.com.inova.sigin.delivery.pedido.dto.PedidoBalcaoResponse;
import br.com.inova.sigin.delivery.pedido.dto.PedidoOperacaoResponse;
import br.com.inova.sigin.delivery.pedido.dto.PedidoResponse;
import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedidoitem.entity.PedidoItem;
import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {

    public PedidoOperacaoResponse toOperacaoResponse(Pedido pedido, String setor){
        return PedidoOperacaoResponse.builder()
                .id(pedido.getId())
                .clienteNome(pedido.getClienteNome())
                .status(pedido.getStatus().name())
                .observacao(pedido.getObservacao())
                .observacaoOperacao(pedido.getObservacaoOperacao())
                .valorTotal(pedido.getValorTotal())
                .itens(
                        pedido.getItens()
                                .stream()
                                .filter(item -> setor != null &&
                                        setor.equals(
                                                item.getProduto()
                                                        .getCategoria()
                                                        .getSetor()
                                                        .getNome()
                                        )
                                )
                                .map(this::toOperacaoItem)
                                .toList()
                )
                .build();
    }

    private ItemOperacaoResponse toOperacaoItem(PedidoItem item) {
        return ItemOperacaoResponse.builder()
                .produto(item.getProduto().getNome())
                .quantidade(item.getQuantidade())
                .categoria(item.getProduto().getCategoria().getNome())
                .setor(
                        item.getProduto().getCategoria().getSetor() != null
                                ? item.getProduto().getCategoria().getSetor().getNome()
                                : null
                )
                .build();
    }
    public PedidoResponse toResponse(Pedido pedido) {

        return PedidoResponse.builder()
                .id(pedido.getId())
                .clienteNome(pedido.getClienteNome())
                .status(pedido.getStatus().name())
                .valorTotal(pedido.getValorTotal())
                .observacaoOperacao(pedido.getObservacaoOperacao())
                .build();
    }
    public PedidoBalcaoResponse toBalcaoResponse(Pedido pedido) {

        return PedidoBalcaoResponse.builder()
                .id(pedido.getId())
                .clienteNome(pedido.getClienteNome())
                .status(pedido.getStatus().name())
                .valorTotal(pedido.getValorTotal())
                .formaPagamento(pedido.getFormaPagamento())
                .itens(
                        pedido.getItens()
                                .stream()
                                .map(this::toOperacaoItem)
                                .toList()
                )
                .build();
    }

    public PedidoOperacaoResponse toEntregaResponse(Pedido pedido){

        return PedidoOperacaoResponse.builder()
                .id(pedido.getId())
                .clienteNome(pedido.getClienteNome())
                .status(pedido.getStatus().name())
                .observacao(pedido.getObservacao())
                .observacaoOperacao(pedido.getObservacaoOperacao())
                .valorTotal(pedido.getValorTotal())
                .itens(
                        pedido.getItens()
                                .stream()
                                .map(this::toOperacaoItem)
                                .toList()
                )
                .build();
    }
}