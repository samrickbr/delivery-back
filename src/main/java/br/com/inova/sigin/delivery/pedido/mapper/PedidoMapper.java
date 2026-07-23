package br.com.inova.sigin.delivery.pedido.mapper;

import br.com.inova.sigin.delivery.pedido.dto.PedidoResponse;
import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {

    public PedidoResponse toResponse(Pedido pedido) {

        return PedidoResponse.builder()
                .id(pedido.getId())
                .clienteNome(pedido.getClienteNome())
                .status(pedido.getStatus().name())
                .valorTotal(pedido.getValorTotal())
                .observacaoOperacao(pedido.getObservacaoOperacao())
                .build();
    }
}