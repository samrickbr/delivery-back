package br.com.inova.sigin.delivery.pedido.exception;

import br.com.inova.sigin.delivery.pedidoitem.entity.PedidoItem;

import java.util.List;

public class ProducoesPendentesException extends RuntimeException {

    private final List<PedidoItem> itens;

    public ProducoesPendentesException(List<PedidoItem> itens) {
        super("Existem itens de produção pendentes.");
        this.itens = List.copyOf(itens);
    }

    public List<PedidoItem> getItens() {
        return itens;
    }
}
