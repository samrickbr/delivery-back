package br.com.inova.sigin.delivery.pedido.dto;

import lombok.Data;

import java.util.List;

@Data
public class PedidoRequest {

    private Long clienteId;

    private String observacao;

    private List<PedidoItemRequest> itens;
}