package br.com.inova.sigin.delivery.core.dto;

import java.math.BigDecimal;

public record PedidoPagamentoRequest(
        Long formaPagamentoId,
        BigDecimal valor
) {
}