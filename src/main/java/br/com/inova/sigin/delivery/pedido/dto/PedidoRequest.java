package br.com.inova.sigin.delivery.pedido.dto;

import lombok.Data;

import java.util.List;

@Data
public class PedidoRequest {

    private Long clienteId;

    private String clienteNome;

    private String clienteWhatsapp;

    private String observacao;

    private List<PedidoPagamentoRequest> pagamentos;

    private String tipoRecebimento;

    private Long enderecoId;

    private List<PedidoItemRequest> itens;
}