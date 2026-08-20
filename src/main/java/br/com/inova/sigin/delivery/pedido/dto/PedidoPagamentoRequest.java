package br.com.inova.sigin.delivery.pedido.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PedidoPagamentoRequest {

    private Long formaPagamentoId;

    private BigDecimal valor;
}