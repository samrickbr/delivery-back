package br.com.inova.sigin.delivery.produto.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProdutoResponse {

    private Long id;

    private Long categoriaId;

    private String categoria;

    private String nome;

    private String descricao;

    private BigDecimal preco;

    private String imagem;

    private Boolean disponivel;

    private Boolean ativo;
}