package br.com.inova.sigin.delivery.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaRequest {

    private String nome;

    private String tipoDocumento;

    private String documento;

    private String telefone;

    private String email;

    private String observacao;
}