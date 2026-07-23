package br.com.inova.sigin.delivery.configuracao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConfiguracaoRequest {

    @NotBlank
    private String empresa;

    @NotBlank
    private String telefone;

    @NotBlank
    private String whatsapp;

    @NotNull
    private BigDecimal taxaEntrega;
}