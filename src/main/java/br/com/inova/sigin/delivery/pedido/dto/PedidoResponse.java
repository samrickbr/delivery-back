package br.com.inova.sigin.delivery.pedido.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PedidoResponse {

    private Long id;

    private String clienteNome;

    private String status;

    private BigDecimal valorTotal;

    private String observacaoOperacao;

}