package br.com.inova.sigin.delivery.pedido.dto;

import br.com.inova.sigin.delivery.pedidoitem.enums.StatusOperacao;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemOperacaoResponse {

    private Long id;

    private String produto;

    private Integer quantidade;

    private String categoria;

    private String setor;

    private String statusOperacao;
}