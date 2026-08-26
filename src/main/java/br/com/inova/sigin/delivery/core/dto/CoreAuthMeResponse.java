package br.com.inova.sigin.delivery.core.dto;

import lombok.Data;

import java.util.List;

@Data
public class CoreAuthMeResponse {

    private Long id;

    private String login;

    private Boolean ativo;

    private PessoaResponse pessoa;

    private List<CorePerfilResponse> perfis;

    private List<CorePermissaoResponse> permissoes;
}