package br.com.inova.sigin.delivery.core.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CatalogoItemResponse {

    private Long canalVendaId;

    private String imagem;

    private BigDecimal precoVenda;

    private String produto;

    private Long produtoId;
}