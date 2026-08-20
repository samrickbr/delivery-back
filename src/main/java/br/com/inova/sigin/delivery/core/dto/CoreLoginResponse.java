package br.com.inova.sigin.delivery.core.dto;

import lombok.Data;

@Data
public class CoreLoginResponse {

    private String token;

    private String tipo;

    private Long clienteId;
}