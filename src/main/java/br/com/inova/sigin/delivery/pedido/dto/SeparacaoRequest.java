package br.com.inova.sigin.delivery.pedido.dto;

import lombok.Data;

import java.util.List;

@Data
public class SeparacaoRequest {

    private List<SeparacaoItemRequest> itens;
}