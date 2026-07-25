package br.com.inova.sigin.delivery.produto.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CardapioResponse {

    private Long id;

    private String nome;

    private String descricao;

    private String categoria;

    private BigDecimal preco;

    private String imagem;
}