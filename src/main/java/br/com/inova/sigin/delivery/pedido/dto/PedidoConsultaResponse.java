package br.com.inova.sigin.delivery.pedido.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoConsultaResponse(
        Long id,
        Long corePedidoId,
        String numero,

        Long clienteId,
        Object cliente,
        String clienteWhatsapp,

        Object endereco,
        String tipoRecebimento,

        Long canalVendaId,
        Object canalVenda,

        LocalDateTime dataPedido,

        BigDecimal valorProdutos,
        BigDecimal valorTotal,
        BigDecimal taxaEntrega,

        String status,
        Boolean ativo,
        String observacao,

        List<PagamentoConsultaResponse> pagamentos,
        List<ItemConsultaResponse> itens
) {

    public record PagamentoConsultaResponse(
            Long id,
            Long formaPagamentoId,
            BigDecimal valor
    ) {}

    public record ItemConsultaResponse(
            Long id,
            Long coreItemId,
            Long produtoId,
            String produto,
            BigDecimal quantidade,
            BigDecimal valorUnitario,
            BigDecimal valorTotal,
            String setor,

            String statusOperacao,
            Boolean separado,
            String motivoCancelamento,
            LocalDateTime canceladoEm,
            String canceladoPor
    ) {}
}