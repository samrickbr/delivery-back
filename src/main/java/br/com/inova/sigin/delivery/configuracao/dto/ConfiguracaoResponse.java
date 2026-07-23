package br.com.inova.sigin.delivery.configuracao.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ConfiguracaoResponse {

    private Long id;

    private String empresa;

    private String telefone;

    private String whatsapp;

    private BigDecimal taxaEntrega;

    private Boolean ativo;
}