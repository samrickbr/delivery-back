package br.com.inova.sigin.delivery.core.dto;

import lombok.Data;

@Data
public class CoreAuthMeResponse {

    private Long id;

    private String login;

    private Boolean ativo;

    private PessoaResponse pessoa;
}