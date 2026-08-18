package br.com.inova.sigin.delivery.pedido.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PedidoResponse {

    private Long id;

    private Long clienteId;

    private String status;

    private List<ItemOperacaoResponse> itens;

    private BigDecimal valorTotal;

    private String observacaoOperacao;

    private List<PedidoHistoricoResponse> historico;

}