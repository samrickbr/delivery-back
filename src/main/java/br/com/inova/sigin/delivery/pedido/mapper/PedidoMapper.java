package br.com.inova.sigin.delivery.pedido.mapper;

import br.com.inova.sigin.delivery.pedido.dto.*;
import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import br.com.inova.sigin.delivery.pedidohistorico.entity.PedidoHistorico;
import br.com.inova.sigin.delivery.pedidoitem.entity.PedidoItem;
import br.com.inova.sigin.delivery.pedidoitem.enums.StatusOperacao;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PedidoMapper {

    public PedidoOperacaoResponse toOperacaoResponse(
            Pedido pedido,
            String setor
    ) {

        return PedidoOperacaoResponse.builder()
                .id(pedido.getId())
                .clienteNome(pedido.getClienteNome())
                .status(pedido.getStatus().name())
                .observacao(pedido.getObservacao())
                .observacaoOperacao(pedido.getObservacaoOperacao())
                .valorTotal(pedido.getValorTotal())
                .itens(
                        pedido.getItens()
                                .stream()
                                .filter(item ->
                                        setor != null
                                                && setor.equals(item.getSetor())
                                                && item.getStatusOperacao()
                                                != StatusOperacao.FINALIZADO
                                                && item.getStatusOperacao()
                                                != StatusOperacao.CANCELADO
                                )
                                .map(this::toOperacaoItem)
                                .toList()
                )
                .build();
    }

    private ItemOperacaoResponse toOperacaoItem(PedidoItem item) {

        return ItemOperacaoResponse.builder()
                .id(item.getId())
                .produtoId(item.getCoreProdutoId())
                .produto(item.getProdutoNome())
                .quantidade(item.getQuantidade())
                .valorUnitario(item.getValorUnitario())
                .valorTotal(item.getValorTotal())
                .categoria(null)
                .setor(item.getSetor())
                .statusOperacao(item.getStatusOperacao().name())
                .build();
    }

    public PedidoResponse toResponse(Pedido pedido) {

        return PedidoResponse.builder()
                .id(pedido.getId())
                .clienteNome(pedido.getClienteNome())
                .status(pedido.getStatus().name())
                .valorTotal(pedido.getValorTotal())
                .formaPagamento(pedido.getFormaPagamento())
                .observacaoOperacao(pedido.getObservacaoOperacao())
                .dataCriacao(pedido.getDataCriacao())
                .tipoRecebimento(pedido.getTipoRecebimento())
                .itens(
                        pedido.getItens()
                                .stream()
                                .map(this::toOperacaoItem)
                                .toList()
                )
                .historico(
                        pedido.getHistorico()
                                .stream()
                                .map(this::toHistoricoResponse)
                                .toList()
                )
                .build();
    }

        public PedidoBalcaoResponse toBalcaoResponse(Pedido pedido, String numero) {

        return PedidoBalcaoResponse.builder()
                .id(pedido.getId())
                                .numero(numero)
                .clienteNome(pedido.getClienteNome())
                .status(pedido.getStatus().name())
                .valorTotal(pedido.getValorTotal())
                .formaPagamento(pedido.getFormaPagamento())
                .aguardaConferencia(
                        precisaConferencia(pedido)
                                && pedido.getStatus() == StatusPedido.FINALIZADO
                )
                .itens(
                        pedido.getItens()
                                .stream()
                                .map(this::toOperacaoItem)
                                .toList()
                )
                .historico(
                        pedido.getHistorico() == null
                                ? List.of()
                                : pedido.getHistorico()
                                .stream()
                                .map(this::toHistoricoResponse)
                                .toList()
                )
                .build();

    }

    public PedidoOperacaoResponse toEntregaResponse(Pedido pedido) {

        return PedidoOperacaoResponse.builder()
                .id(pedido.getId())
                .clienteNome(pedido.getClienteNome())
                .status(pedido.getStatus().name())
                .observacao(pedido.getObservacao())
                .observacaoOperacao(pedido.getObservacaoOperacao())
                .valorTotal(pedido.getValorTotal())
                .itens(
                        pedido.getItens()
                                .stream()
                                .map(this::toOperacaoItem)
                                .toList()
                )
                .build();
    }

    private boolean precisaConferencia(Pedido pedido) {

        return pedido.getItens()
                .stream()
                .allMatch(item ->
                        item.getStatusOperacao()
                                == StatusOperacao.FINALIZADO
                                ||
                                item.getStatusOperacao()
                                        == StatusOperacao.CANCELADO
                );
    }

    public PedidoHistoricoResponse toHistoricoResponse(
            PedidoHistorico historico
    ) {

        return PedidoHistoricoResponse.builder()
                .dataHora(historico.getDataHora())
                .usuario(historico.getUsuarioNome())
                .setor(historico.getSetor())
                .acao(historico.getAcao())
                .descricao(historico.getDescricao())
                .build();
    }
}