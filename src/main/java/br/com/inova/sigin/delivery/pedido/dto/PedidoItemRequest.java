package br.com.inova.sigin.delivery.pedido.dto;

import lombok.Data;

@Data
public class PedidoItemRequest {

    private Long produtoId;

    private Integer quantidade;
}