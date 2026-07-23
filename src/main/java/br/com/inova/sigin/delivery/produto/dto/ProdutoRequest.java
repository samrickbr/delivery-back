package br.com.inova.sigin.delivery.produto.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoRequest {

    private Long categoriaId;

    private String nome;

    private String descricao;

    private BigDecimal preco;

    private String imagem;
}