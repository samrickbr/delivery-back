package br.com.inova.sigin.delivery.pedido.service;

import br.com.inova.sigin.delivery.core.client.CoreClient;
import br.com.inova.sigin.delivery.core.dto.CoreAuthMeResponse;
import br.com.inova.sigin.delivery.pedido.dto.*;
import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import br.com.inova.sigin.delivery.pedido.mapper.PedidoMapper;
import br.com.inova.sigin.delivery.pedido.repository.PedidoRepository;
import br.com.inova.sigin.delivery.pedidohistorico.repository.PedidoHistoricoRepository;
import br.com.inova.sigin.delivery.pedidoitem.entity.PedidoItem;
import br.com.inova.sigin.delivery.pedidoitem.enums.StatusOperacao;
import br.com.inova.sigin.delivery.pedidoitem.repository.PedidoItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoConsultaService {

    private final PedidoRepository repository;
    private final PedidoItemRepository itemRepository;
    private final PedidoHistoricoRepository historicoRepository;
    private final PedidoMapper mapper;
    private final CoreClient coreClient;

    public List<PedidoResponse> listar() {
        return repository.findAllByOrderByDataCriacaoAsc()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<PedidoResponse> listarPorStatus(StatusPedido status) {
        return repository.findByStatusOrderByDataCriacaoAsc(status)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<PedidoResponse> listarFinalizados() {
        return repository.findByStatusInOrderByStatusAlteradoEmAsc(
                        List.of(
                                StatusPedido.ENTREGUE,
                                StatusPedido.FINALIZADO,
                                StatusPedido.CANCELADO
                        )
                )
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<PedidoResponse> listarEntrega() {
        return repository.findByStatusInOrderByStatusAlteradoEmAsc(
                        List.of(
                                StatusPedido.SEPARADO,
                                StatusPedido.SAIU_ENTREGA
                        )
                )
                .stream()
                .filter(pedido ->
                        "ENTREGA".equalsIgnoreCase(pedido.getTipoRecebimento())
                )
                .map(mapper::toResponse)
                .toList();
    }

    public List<PedidoResponse> listarEntregues() {
        return repository.findByStatusInOrderByStatusAlteradoEmAsc(
                        List.of(
                                StatusPedido.ENTREGUE,
                                StatusPedido.CANCELADO
                        )
                )
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<PedidoBalcaoResponse> listarBalcao() {
        return repository
                .findByStatusInOrderByDataCriacaoAsc(
                        List.of(
                                StatusPedido.RECEBIDO,
                                StatusPedido.FINALIZADO,
                                StatusPedido.AGUARDANDO_SEPARACAO
                        )
                )
                .stream()
                .map(mapper::toBalcaoResponse)
                .filter(pedido ->
                        pedido.getItens()
                                .stream()
                                .anyMatch(item ->
                                        "BALCAO".equalsIgnoreCase(item.getSetor())
                                                && !"CANCELADO".equalsIgnoreCase(item.getStatusOperacao())
                                )
                )
                .toList();
    }

    public List<PedidoBalcaoResponse> listarSeparacao() {
        return repository
                .findByStatusInOrderByStatusAlteradoEmAsc(
                        List.of(StatusPedido.AGUARDANDO_SEPARACAO)
                )
                .stream()
                .map(mapper::toBalcaoResponse)
                .toList();
    }

    public List<PedidoBalcaoResponse> listarRetirada() {
        return repository
                .findByStatusInOrderByStatusAlteradoEmAsc(
                        List.of(StatusPedido.SEPARADO)
                )
                .stream()
                .filter(pedido ->
                        "RETIRADA".equalsIgnoreCase(pedido.getTipoRecebimento())
                )
                .map(mapper::toBalcaoResponse)
                .toList();
    }

    public List<PedidoResponse> listarMeus(String authorization) {
        CoreAuthMeResponse autenticado = buscarUsuarioAutenticado(authorization);
        Long clienteId = autenticado.getPessoa().getId();

        return repository.findByClienteIdOrderByDataCriacaoDesc(clienteId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<PedidoOperacaoResponse> pedidosCozinha(String setor) {
        List<PedidoItem> itens =
                itemRepository.findBySetorAndStatusOperacaoIn(
                        setor,
                        List.of(
                                StatusOperacao.APROVADO,
                                StatusOperacao.PENDENTE,
                                StatusOperacao.EM_PRODUCAO
                        )
                );

        return itens.stream()
                .map(PedidoItem::getPedido)
                .distinct()
                .filter(pedido ->
                        pedido.getStatus() != StatusPedido.RECEBIDO &&
                                pedido.getStatus() != StatusPedido.ENTREGUE &&
                                pedido.getStatus() != StatusPedido.CANCELADO &&
                                pedido.getStatus() != StatusPedido.FINALIZADO
                )
                .map(pedido -> mapper.toOperacaoResponse(pedido, setor))
                .filter(pedido -> !pedido.getItens().isEmpty())
                .toList();
    }

    public List<PedidoOperacaoResponse> pedidosOperacaoCozinha() {
        return repository
                .findByStatusInOrderByStatusAlteradoEmAsc(
                        List.of(
                                StatusPedido.APROVADO,
                                StatusPedido.PENDENTE,
                                StatusPedido.EM_PRODUCAO
                        )
                )
                .stream()
                .map(pedido -> mapper.toOperacaoResponse(pedido, null))
                .toList();
    }

    public List<PedidoOperacaoResponse> listarEntregaOperacao() {
        return repository
                .findByStatusInOrderByStatusAlteradoEmAsc(
                        List.of(
                                StatusPedido.SEPARADO,
                                StatusPedido.SAIU_ENTREGA
                        )
                )
                .stream()
                .filter(pedido ->
                        "ENTREGA".equalsIgnoreCase(pedido.getTipoRecebimento())
                )
                .map(mapper::toEntregaResponse)
                .toList();
    }

    public List<PedidoHistoricoResponse> listarHistorico(Long pedidoId) {
        return historicoRepository
                .findByPedidoIdOrderByDataHoraAsc(pedidoId)
                .stream()
                .map(mapper::toHistoricoResponse)
                .toList();
    }

    private CoreAuthMeResponse buscarUsuarioAutenticado(String authorization) {
        CoreAuthMeResponse autenticado = coreClient.buscarAutenticado(authorization);

        if (autenticado == null || autenticado.getId() == null) {
            throw new IllegalStateException(
                    "Não foi possível identificar o usuário autenticado."
            );
        }

        if (autenticado.getPessoa() == null || autenticado.getPessoa().getId() == null) {
            throw new IllegalStateException(
                    "Usuário autenticado não possui pessoa vinculada."
            );
        }

        return autenticado;
    }

    public PedidoConsultaResponse buscarPorId(Long id, String token) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        br.com.inova.sigin.delivery.core.dto.PedidoResponse corePedido =
                coreClient.buscarPedido(pedido.getCorePedidoId(), token);

        List<PedidoItem> itensLocais = itemRepository.findByPedidoId(id);

        Map<Long, PedidoItem> itensPorCoreId = itensLocais.stream()
                .collect(Collectors.toMap(
                        PedidoItem::getCoreItemId,
                        item -> item
                ));

        List<PedidoConsultaResponse.ItemConsultaResponse> itens =
                corePedido.itens().stream()
                        .map(itemCore -> {
                            PedidoItem itemLocal = itensPorCoreId.get(itemCore.id());

                            return new PedidoConsultaResponse.ItemConsultaResponse(
                                    itemCore.id(),
                                    itemCore.id(),
                                    itemCore.produtoId(),
                                    itemCore.produto(),
                                    itemCore.quantidade(),
                                    itemCore.valorUnitario(),
                                    itemCore.valorTotal(),
                                    itemCore.setor(),

                                    itemLocal != null
                                            ? itemLocal.getStatusOperacao().name()
                                            : null,

                                    itemLocal != null
                                            ? itemLocal.getSeparado()
                                            : false,

                                    itemLocal != null
                                            ? itemLocal.getMotivoCancelamento()
                                            : null,

                                    itemLocal != null
                                            ? itemLocal.getCanceladoEm()
                                            : null,

                                    itemLocal != null
                                            ? itemLocal.getCanceladoPor()
                                            : null
                            );
                        })
                        .toList();

        List<PedidoConsultaResponse.PagamentoConsultaResponse> pagamentos =
                corePedido.pagamentos().stream()
                        .map(pagamento -> new PedidoConsultaResponse.PagamentoConsultaResponse(
                                pagamento.id(),
                                pagamento.formaPagamentoId(),
                                pagamento.valor()
                        ))
                        .toList();

        return new PedidoConsultaResponse(
                pedido.getId(),
                pedido.getCorePedidoId(),
                corePedido.numero(),

                corePedido.clienteId(),
                corePedido.cliente(),
                corePedido.clienteWhatsapp(),

                corePedido.endereco(),
                corePedido.tipoRecebimento(),

                corePedido.canalVendaId(),
                corePedido.canalVenda(),

                corePedido.dataPedido(),

                corePedido.valorProdutos(),
                corePedido.valorTotal(),
                corePedido.taxaEntrega(),

                corePedido.status(),
                corePedido.ativo(),
                corePedido.observacao(),

                pagamentos,
                itens
        );
    }

    public PedidoSituacaoFinanceiraResponse consultarSituacaoFinanceira(
            Long pedidoId
    ) {
        return coreClient.consultarSituacaoFinanceira(pedidoId);
    }
}