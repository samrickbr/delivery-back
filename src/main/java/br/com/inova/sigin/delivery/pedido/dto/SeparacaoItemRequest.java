package br.com.inova.sigin.delivery.pedido.dto;

import lombok.Data;

@Data
public class SeparacaoItemRequest {

    private Long itemId;

    private Boolean separado;
}