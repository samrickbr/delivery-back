package br.com.inova.sigin.delivery.pedido.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ItemOperacaoResponse {

    private Long id;

    private Long produtoId;

    private String produto;

    private Integer quantidade;

    private BigDecimal valorUnitario;

    private BigDecimal valorTotal;

    private String categoria;

    private String setor;

    private String statusOperacao;
}