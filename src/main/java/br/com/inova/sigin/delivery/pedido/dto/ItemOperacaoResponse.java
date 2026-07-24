package br.com.inova.sigin.delivery.pedido.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemOperacaoResponse {

    private String produto;

    private Integer quantidade;

    private String categoria;

    private String setor;
}