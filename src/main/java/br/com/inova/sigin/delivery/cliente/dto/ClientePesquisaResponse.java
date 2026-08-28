package br.com.inova.sigin.delivery.cliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientePesquisaResponse {

    private Long id;
    private String nome;
    private String telefone;
    private String documento;
    private String email;
}
