package br.com.inova.sigin.delivery.core.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        String numero,
        Long clienteId,
        Object cliente,
        Object endereco,
        String tipoRecebimento,
        Long canalVendaId,
        Object canalVenda,
        LocalDateTime dataPedido,
        BigDecimal valorProdutos,
        BigDecimal valorTotal,
        BigDecimal taxaEntrega,
        String status,
        List<PedidoPagamentoResponse> pagamentos,
        List<PedidoItemResponse> itens,
        Boolean ativo,
        String observacao
) {
}