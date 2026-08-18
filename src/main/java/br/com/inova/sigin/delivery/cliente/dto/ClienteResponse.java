package br.com.inova.sigin.delivery.cliente.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteResponse {

    private Long id;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;
}