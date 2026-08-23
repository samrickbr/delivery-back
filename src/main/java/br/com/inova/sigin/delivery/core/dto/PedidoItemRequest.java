package br.com.inova.sigin.delivery.core.dto;

public record PedidoItemRequest(
        Long produtoId,
        Integer quantidade
) {
}