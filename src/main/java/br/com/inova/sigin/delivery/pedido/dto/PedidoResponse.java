package br.com.inova.sigin.delivery.pedido.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PedidoResponse {

    private Long id;

    private String clienteNome;

    private String status;

    private List<ItemOperacaoResponse> itens;

    private BigDecimal valorTotal;

    private String formaPagamento;

    private String observacaoOperacao;

    private LocalDateTime dataCriacao;

    private String tipoRecebimento;

    private List<PedidoHistoricoResponse> historico;
}