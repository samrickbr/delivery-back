package br.com.inova.sigin.delivery.core.dto;

import java.math.BigDecimal;
import java.util.List;

public record PedidoRequest(
        Long clienteId,
        Long enderecoId,
        String tipoRecebimento,
        Long canalVendaId,
        List<PedidoPagamentoRequest> pagamentos,
        String observacao,
        List<PedidoItemRequest> itens
) {
}