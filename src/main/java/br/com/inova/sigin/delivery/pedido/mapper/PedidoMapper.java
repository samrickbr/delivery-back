package br.com.inova.sigin.delivery.pedido.mapper;

import br.com.inova.sigin.delivery.pedido.dto.ItemOperacaoResponse;
import br.com.inova.sigin.delivery.pedido.dto.PedidoBalcaoResponse;
import br.com.inova.sigin.delivery.pedido.dto.PedidoOperacaoResponse;
import br.com.inova.sigin.delivery.pedido.dto.PedidoResponse;
import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedidoitem.entity.PedidoItem;
import br.com.inova.sigin.delivery.pedidoitem.enums.StatusOperacao;
import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {

    public PedidoOperacaoResponse toOperacaoResponse(Pedido pedido, String setor) {

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
                                .filter(item ->
                                        setor != null &&
                                                setor.equals(
                                                        item.getProduto()
                                                                .getCategoria()
                                                                .getSetor()
                                                                .getNome()
                                                ) &&
                                                item.getStatusOperacao() != StatusOperacao.FINALIZADO &&
                                                item.getStatusOperacao() != StatusOperacao.CANCELADO
                                )
                                .map(this::toOperacaoItem)
                                .toList()
                )
                .build();
    }

    private ItemOperacaoResponse toOperacaoItem(PedidoItem item) {

        return ItemOperacaoResponse.builder()
                .id(item.getId())
                .produto(item.getProduto().getNome())
                .quantidade(item.getQuantidade())
                .categoria(item.getProduto().getCategoria().getNome())
                .setor(
                        item.getProduto()
                                .getCategoria()
                                .getSetor()
                                .getNome()
                )
                .statusOperacao(item.getStatusOperacao().name())
                .build();
    }
    public PedidoResponse toResponse(Pedido pedido) {

        return PedidoResponse.builder()
                .id(pedido.getId())
                .clienteNome(pedido.getClienteNome())
                .status(pedido.getStatus().name())
                .valorTotal(pedido.getValorTotal())
                .observacaoOperacao(pedido.getObservacaoOperacao())
                .itens(
                        pedido.getItens()
                                .stream()
                                .map(this::toOperacaoItem)
                                .toList()
                )
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
    private String getSetor(PedidoItem item) {
        return item.getProduto()
                .getCategoria()
                .getSetor()
                .getNome();
    }
}