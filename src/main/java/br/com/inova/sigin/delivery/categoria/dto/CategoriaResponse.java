package br.com.inova.sigin.delivery.categoria.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoriaResponse {

    private Long id;

    private String nome;

    private String descricao;

    private Boolean ativo;
}