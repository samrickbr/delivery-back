package br.com.inova.sigin.delivery.core.dto;

import java.math.BigDecimal;

public record PedidoPagamentoResponse(
        Long id,
        Long formaPagamentoId,
        BigDecimal valor
) {
}