package br.com.inova.sigin.delivery.cliente.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteLoginResponse {

    private String token;
    private String tipo;
}