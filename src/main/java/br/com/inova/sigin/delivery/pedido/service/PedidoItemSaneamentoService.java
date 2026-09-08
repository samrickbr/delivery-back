package br.com.inova.sigin.delivery.pedido.service;

import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedidoitem.entity.PedidoItem;
import br.com.inova.sigin.delivery.pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PedidoItemSaneamentoService {

    private final PedidoRepository pedidoRepository;

    @Transactional
    public void validarIntegridade(Pedido pedido) {
        if (pedido == null || pedido.getCorePedidoId() == null) {
            return;
        }

        for (PedidoItem item : pedido.getItens()) {
            if (item.getCoreItemId() == null) {
                throw new IllegalStateException(
                        "Item local sem referência ao item correspondente no SIGIN Core."
                );
            }
        }
    }
}