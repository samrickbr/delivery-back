package br.com.inova.sigin.delivery.pedido.service;

import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import org.springframework.stereotype.Service;

@Service
public class StatusPedidoService {

    public void aprovar(Pedido pedido) {
        pedido.setStatus(StatusPedido.APROVADO);
    }

    public void iniciarProducao(Pedido pedido) {
        pedido.setStatus(StatusPedido.EM_PRODUCAO);
    }

    public void sairEntrega(Pedido pedido) {
        pedido.setStatus(StatusPedido.SAIU_ENTREGA);
    }

    public void finalizar(Pedido pedido) {
        pedido.setStatus(StatusPedido.FINALIZADO);
    }

    public void cancelar(Pedido pedido) {
        pedido.setStatus(StatusPedido.CANCELADO);
    }
}