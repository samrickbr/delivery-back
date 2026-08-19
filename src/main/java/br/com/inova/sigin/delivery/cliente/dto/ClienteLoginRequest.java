package br.com.inova.sigin.delivery.cliente.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClienteLoginRequest {

    @NotBlank
    private String cpf;

    @NotBlank
    private String senha;
}