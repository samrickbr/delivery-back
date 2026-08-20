package br.com.inova.sigin.delivery.core.dto;

import lombok.Data;

@Data
public class PessoaEnderecoResponse {

    private Long id;

    private String cep;

    private String logradouro;

    private String numero;

    private String complemento;

    private String bairro;

    private String cidade;

    private String uf;

    private Boolean principal;
}