package br.com.inova.sigin.delivery.pedido.dto;

import lombok.Data;

import java.util.List;

@Data
public class PedidoRequest {

    private String clienteNome;

    private String clienteWhatsapp;

    private String observacao;

    private String formaPagamento;

    private List<PedidoItemRequest> itens;
}