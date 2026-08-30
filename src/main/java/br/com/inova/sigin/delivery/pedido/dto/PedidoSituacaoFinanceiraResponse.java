package br.com.inova.sigin.delivery.pedido.dto;

import java.math.BigDecimal;

public record PedidoSituacaoFinanceiraResponse(
        Long pedidoId,
        BigDecimal valorTotal,
        BigDecimal valorPago,
        BigDecimal saldoPendente,
        BigDecimal valorExcedente,
        String situacao
) {
}