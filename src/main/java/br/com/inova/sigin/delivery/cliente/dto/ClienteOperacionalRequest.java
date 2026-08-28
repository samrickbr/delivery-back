package br.com.inova.sigin.delivery.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClienteOperacionalRequest {

    @NotBlank
    private String nome;

    @NotBlank
    private String documento;

    @NotBlank
    private String telefone;

    @Email
    private String email;
}

