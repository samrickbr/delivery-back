package br.com.inova.sigin.delivery.pedido.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
    @Builder
    public class PedidoBalcaoResponse {

        private Long id;

        private String clienteNome;

        private String status;

        private BigDecimal valorTotal;

        private String formaPagamento;

        private List<ItemOperacaoResponse> itens;
    }
