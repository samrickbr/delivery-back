package br.com.inova.sigin.delivery.pedido.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PedidoOperacaoResponse {

    private Long id;

    private String clienteNome;

    private String status;

    private String observacao;

    private String observacaoOperacao;

    private List<ItemOperacaoResponse> itens;
}