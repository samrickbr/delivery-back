package br.com.inova.sigin.delivery.pedido.service;

import br.com.inova.sigin.delivery.configuracao.entity.Configuracao;
import br.com.inova.sigin.delivery.configuracao.repository.ConfiguracaoRepository;
import br.com.inova.sigin.delivery.core.client.CoreClient;
import br.com.inova.sigin.delivery.core.config.CoreClientProperties;
import br.com.inova.sigin.delivery.core.dto.CoreAuthMeResponse;
import br.com.inova.sigin.delivery.core.dto.PedidoItemRequest;
import br.com.inova.sigin.delivery.core.dto.PedidoPagamentoRequest;
import br.com.inova.sigin.delivery.pedido.dto.*;
import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import br.com.inova.sigin.delivery.pedido.mapper.PedidoMapper;
import br.com.inova.sigin.delivery.pedido.repository.PedidoRepository;
import br.com.inova.sigin.delivery.pedidohistorico.repository.PedidoHistoricoRepository;
import br.com.inova.sigin.delivery.pedidohistorico.service.PedidoHistoricoService;
import br.com.inova.sigin.delivery.pedidoitem.entity.PedidoItem;
import br.com.inova.sigin.delivery.pedidoitem.enums.StatusOperacao;
import br.com.inova.sigin.delivery.pedidoitem.repository.PedidoItemRepository;
import br.com.inova.sigin.delivery.produto.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository repository;
    private final PedidoItemRepository itemRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoMapper mapper;
    private final StatusPedidoService statusService;
    private final PedidoHistoricoService historicoService;
    private final PedidoHistoricoRepository historicoRepository;
    private final ConfiguracaoRepository configuracaoRepository;
    private final CoreClient coreClient;
    private final CoreClientProperties coreClientProperties;
    private final PedidoProjecaoService pedidoProjecaoService;

    public PedidoResponse criar(
            PedidoRequest request,
            String authorization
    ) {
        CoreAuthMeResponse autenticado =
                coreClient.buscarAutenticado(authorization);

        if (autenticado == null
                || autenticado.getPessoa() == null
                || autenticado.getPessoa().getId() == null) {
            throw new IllegalStateException(
                    "Não foi possível identificar o cliente autenticado."
            );
        }

        Long clienteId = autenticado.getPessoa().getId();

        if (request.getItens() == null || request.getItens().isEmpty()) {
            throw new IllegalArgumentException(
                    "O pedido deve possuir pelo menos um item."
            );
        }

        if (request.getPagamentos() == null
                || request.getPagamentos().isEmpty()) {
            throw new IllegalArgumentException(
                    "O pedido deve possuir pelo menos um pagamento."
            );
        }

        String tipoRecebimento = request.getTipoRecebimento();

        if (tipoRecebimento == null || tipoRecebimento.isBlank()) {
            throw new IllegalArgumentException(
                    "Tipo de recebimento não informado."
            );
        }

        tipoRecebimento = tipoRecebimento.trim().toUpperCase();

        Long enderecoId = request.getEnderecoId();

        if ("ENTREGA".equals(tipoRecebimento) && enderecoId == null) {
            throw new IllegalArgumentException(
                    "Endereço é obrigatório para entrega."
            );
        }

        if ("RETIRADA".equals(tipoRecebimento)) {
            enderecoId = null;
        }

        Long canalVendaId = coreClientProperties.getCanalVendaId();

        if (canalVendaId == null) {
            throw new IllegalStateException(
                    "Canal de venda do Delivery não configurado."
            );
        }

        List<PedidoItemRequest> itens = request.getItens()
                .stream()
                .map(item -> new PedidoItemRequest(
                        item.getProdutoId(),
                        item.getQuantidade()
                ))
                .toList();

        List<PedidoPagamentoRequest> pagamentos =
                request.getPagamentos()
                        .stream()
                        .map(pagamento -> new PedidoPagamentoRequest(
                                pagamento.getFormaPagamentoId(),
                                pagamento.getValor()
                        ))
                        .toList();

        br.com.inova.sigin.delivery.core.dto.PedidoRequest coreRequest =
                new br.com.inova.sigin.delivery.core.dto.PedidoRequest(
                        clienteId,
                        enderecoId,
                        tipoRecebimento,
                        canalVendaId,
                        pagamentos,
                        request.getObservacao(),
                        itens
                );

        br.com.inova.sigin.delivery.core.dto.PedidoResponse coreResponse =
                coreClient.criarPedido(coreRequest);

        return pedidoProjecaoService.projetar(
                coreResponse,
                autenticado.getPessoa().getTelefone()
        );
    }

    public Pedido buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow();
    }

    public PedidoResponse salvar(Pedido pedido) {
        return mapper.toResponse(
                repository.save(pedido)
        );
    }

    public PedidoResponse aprovar(Long id) {
        Pedido pedido = buscarEntidade(id);
        statusService.aprovar(pedido);
        pedido.setStatusAlteradoEm(LocalDateTime.now());
        historicoService.registrar(
                pedido,
                "Sistema",
                "BALCAO",
                "APROVADO",
                "Pedido aprovado."
        );
        return salvar(pedido);
    }

    @Transactional
    public PedidoResponse colocarPendente(
            Long id,
            String setor,
            PedidoPendenciaRequest request
    ) {

        Pedido pedido = buscarEntidade(id);

        pedido.getItens()
                .stream()
                .filter(item ->
                        getSetor(item).equals(setor)
                                && item.getStatusOperacao() != StatusOperacao.CANCELADO
                )
                .forEach(item ->
                        item.setStatusOperacao(StatusOperacao.PENDENTE)
                );

        pedido.setObservacaoOperacao(request.getMotivo());
        pedido.setStatusAlteradoEm(LocalDateTime.now());

        repository.save(pedido);

        historicoService.registrar(
                pedido,
                "Sistema",
                setor,
                "PENDENTE",
                request.getMotivo()
        );

        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse cancelar(
            Long id,
            String setor,
            CancelamentoRequest request
    ) {

        Pedido pedido = buscarEntidade(id);

        pedido.getItens()
                .stream()
                .filter(item ->
                        getSetor(item).equals(setor)
                )
                .forEach(item -> {

                    item.setStatusOperacao(
                            StatusOperacao.CANCELADO
                    );

                });

        boolean todosCancelados =
                pedido.getItens()
                        .stream()
                        .allMatch(item ->
                                item.getStatusOperacao() == StatusOperacao.CANCELADO
                        );

        if (todosCancelados) {
            pedido.setStatus(StatusPedido.CANCELADO);
        }

        pedido.setObservacaoOperacao(
                request.getJustificativa()
        );

        pedido.setStatusAlteradoEm(
                LocalDateTime.now()
        );

        repository.save(pedido);

        historicoService.registrar(
                pedido,
                "Sistema",
                setor,
                "SETOR_CANCELADO",
                "Todos os itens do setor foram cancelados. Motivo: "
                        + request.getJustificativa()
        );

        return mapper.toResponse(pedido);
    }

    public List<PedidoResponse> listar() {
        return repository.findAllByOrderByDataCriacaoAsc()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<PedidoResponse> listarPorStatus(
            StatusPedido status
    ) {
        return repository.findByStatusOrderByDataCriacaoAsc(status)
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
                        pedido.getStatus() != StatusPedido.ENTREGUE &&
                                pedido.getStatus() != StatusPedido.CANCELADO &&
                                pedido.getStatus() != StatusPedido.FINALIZADO
                )
                .map(pedido ->
                        mapper.toOperacaoResponse(pedido, setor)
                )
                .filter(pedido ->
                        !pedido.getItens().isEmpty()
                )
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
                .map(mapper::toResponse)
                .toList();
    }

    public PedidoResponse sairParaEntrega(Long id) {
        Pedido pedido = buscarEntidade(id);

        if ("RETIRADA".equalsIgnoreCase(pedido.getTipoRecebimento())) {
            throw new IllegalStateException(
                    "Pedido de retirada não pode sair para entrega."
            );
        }

        pedido.setStatus(StatusPedido.SAIU_ENTREGA);
        pedido.setStatusAlteradoEm(LocalDateTime.now());

        return mapper.toResponse(
                repository.save(pedido)
        );
    }

    public PedidoResponse entregar(Long id) {
        Pedido pedido = buscarEntidade(id);

        pedido.setStatus(StatusPedido.ENTREGUE);
        pedido.setStatusAlteradoEm(LocalDateTime.now());

        repository.save(pedido);

        historicoService.registrar(
                pedido,
                "Sistema",
                "ENTREGA",
                "SAIU_ENTREGA",
                "Pedido saiu para entrega."
        );

        return mapper.toResponse(pedido);
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
                .map(mapper::toEntregaResponse)
                .toList();
    }

    public List<PedidoBalcaoResponse> listarBalcao() {

        return repository
                .findByStatusInOrderByDataCriacaoAsc(
                        List.of(
                                StatusPedido.RECEBIDO,
                                StatusPedido.APROVADO,
                                StatusPedido.EM_PRODUCAO,
                                StatusPedido.PENDENTE,
                                StatusPedido.FINALIZADO,
                                StatusPedido.AGUARDANDO_SEPARACAO
                        )
                )
                .stream()
                .map(mapper::toBalcaoResponse)
                .toList();
    }

    public PedidoResponse separar(Long id) {
        Pedido pedido = repository.findById(id)
                .orElseThrow();

        pedido.setStatus(StatusPedido.SEPARADO);

        repository.save(pedido);

        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse liberarEntrega(
            Long id,
            SeparacaoRequest request
    ) {

        Pedido pedido = buscarEntidade(id);

        for (SeparacaoItemRequest itemRequest : request.getItens()) {

            PedidoItem item = itemRepository.findById(
                    itemRequest.getItemId()
            ).orElseThrow();

            item.setSeparado(itemRequest.getSeparado());

            itemRepository.save(item);
        }

        boolean todosSeparados =
                pedido.getItens()
                        .stream()
                        .filter(item ->
                                item.getStatusOperacao() != StatusOperacao.CANCELADO
                        )
                        .allMatch(PedidoItem::getSeparado);

        if (!todosSeparados) {
            throw new RuntimeException(
                    "Existem itens sem separação."
            );
        }

        pedido.setStatus(StatusPedido.SEPARADO);
        pedido.setStatusAlteradoEm(LocalDateTime.now());

        repository.save(pedido);

        historicoService.registrar(
                pedido,
                "Sistema",
                "BALCAO",
                "SEPARACAO",
                "Pedido separado e liberado para entrega."
        );

        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse iniciarProducao(
            Long id,
            String setor
    ) {

        Pedido pedido = buscarEntidade(id);

        pedido.getItens()
                .stream()
                .filter(item ->
                        getSetor(item).equals(setor)
                                && item.getStatusOperacao() != StatusOperacao.CANCELADO
                )
                .forEach(item ->
                        item.setStatusOperacao(StatusOperacao.EM_PRODUCAO)
                );

        pedido.setStatusAlteradoEm(
                LocalDateTime.now()
        );

        historicoService.registrar(
                pedido,
                "Sistema",
                setor,
                "PRODUCAO_INICIADA",
                "Produção iniciada."
        );

        repository.save(pedido);

        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse finalizar(
            Long id,
            String setor
    ) {

        Pedido pedido = buscarEntidade(id);

        pedido.getItens()
                .stream()
                .filter(item ->
                        getSetor(item).equals(setor)
                                && item.getStatusOperacao() != StatusOperacao.CANCELADO
                )
                .forEach(item ->
                        item.setStatusOperacao(StatusOperacao.FINALIZADO)
                );

        boolean todosFinalizados =
                pedido.getItens()
                        .stream()
                        .allMatch(item ->
                                item.getStatusOperacao() == StatusOperacao.FINALIZADO
                                        || item.getStatusOperacao() == StatusOperacao.CANCELADO
                        );

        if (todosFinalizados) {
            pedido.setStatus(StatusPedido.FINALIZADO);
        }

        pedido.setStatusAlteradoEm(
                LocalDateTime.now()
        );

        repository.save(pedido);

        historicoService.registrar(
                pedido,
                "Sistema",
                setor,
                "FINALIZADO",
                "Setor finalizou a produção."
        );

        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse conferir(Long id) {

        Pedido pedido = buscarEntidade(id);

        pedido.setStatus(StatusPedido.AGUARDANDO_SEPARACAO);
        pedido.setConferenciaEm(LocalDateTime.now());

        // temporário até integrar com o Core
        pedido.setConferenciaPorUsuarioId(1L);

        pedido.setStatusAlteradoEm(LocalDateTime.now());

        repository.save(pedido);

        historicoService.registrar(
                pedido,
                "Sistema",
                "BALCAO",
                "CONFERENCIA",
                "Conferência realizada."
        );

        return mapper.toResponse(pedido);
    }

    public List<PedidoHistoricoResponse> listarHistorico(
            Long pedidoId
    ) {

        return historicoRepository
                .findByPedidoIdOrderByDataHoraAsc(pedidoId)
                .stream()
                .map(mapper::toHistoricoResponse)
                .toList();
    }

    @Transactional
    public PedidoResponse cancelarPedido(
            Long id,
            CancelamentoRequest request
    ) {

        Pedido pedido = buscarEntidade(id);

        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.setStatusAlteradoEm(LocalDateTime.now());

        historicoService.registrar(
                pedido,
                "Sistema",
                "BALCAO",
                "PEDIDO_CANCELADO",
                request.getJustificativa()
        );

        return mapper.toResponse(
                repository.save(pedido)
        );
    }

    @Transactional
    public PedidoResponse cancelarItens(
            Long id,
            String setor,
            CancelamentoItensRequest request
    ) {

        Pedido pedido = buscarEntidade(id);

        for (Long itemId : request.getItens()) {

            PedidoItem item = pedido.getItens()
                    .stream()
                    .filter(i -> i.getId().equals(itemId))
                    .findFirst()
                    .orElseThrow();

            String setorItem = getSetor(item);

            if (!setorItem.equals(setor)
                    && !setor.equals("BALCAO")) {
                throw new RuntimeException(
                        "Usuário não pode cancelar este item."
                );
            }

            item.setStatusOperacao(
                    StatusOperacao.CANCELADO
            );

            item.setMotivoCancelamento(
                    request.getJustificativa()
            );

            item.setCanceladoEm(
                    LocalDateTime.now()
            );

            item.setCanceladoPor("Sistema");

            historicoService.registrar(
                    pedido,
                    "Sistema",
                    setor,
                    "ITEM_CANCELADO",
                    item.getQuantidade()
                            + "x "
                            + item.getProdutoNome()
                            + " - Motivo: "
                            + request.getJustificativa()
            );
        }

        pedido.setStatusAlteradoEm(LocalDateTime.now());

        repository.save(pedido);

        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse cancelarPedidoCompleto(
            Long id,
            String justificativa
    ) {

        Pedido pedido = buscarEntidade(id);

        pedido.getItens().forEach(item -> {
            item.setStatusOperacao(
                    StatusOperacao.CANCELADO
            );

            item.setMotivoCancelamento(
                    justificativa
            );

            item.setCanceladoEm(
                    LocalDateTime.now()
            );

            item.setCanceladoPor("Sistema");
        });

        pedido.setStatus(StatusPedido.CANCELADO);

        pedido.setStatusAlteradoEm(
                LocalDateTime.now()
        );

        historicoService.registrar(
                pedido,
                "Sistema",
                "BALCAO",
                "PEDIDO_CANCELADO",
                justificativa
        );

        return mapper.toResponse(
                repository.save(pedido)
        );
    }

    private String getSetor(PedidoItem item) {
        return item.getSetor();
    }

    public List<PedidoBalcaoResponse> listarSeparacao() {

        return repository
                .findByStatusInOrderByStatusAlteradoEmAsc(
                        List.of(
                                StatusPedido.AGUARDANDO_SEPARACAO
                        )
                )
                .stream()
                .map(mapper::toBalcaoResponse)
                .toList();
    }

    private BigDecimal calcularTaxaEntrega(
            PedidoRequest request
    ) {

        if ("RETIRADA".equalsIgnoreCase(
                request.getTipoRecebimento()
        )) {
            return BigDecimal.ZERO;
        }

        if (!"ENTREGA".equalsIgnoreCase(
                request.getTipoRecebimento()
        )) {
            throw new IllegalArgumentException(
                    "Tipo de recebimento inválido."
            );
        }

        if (request.getEnderecoId() == null) {
            throw new IllegalArgumentException(
                    "Endereço é obrigatório para entrega."
            );
        }

        Configuracao configuracao = configuracaoRepository
                .findFirstByAtivoTrue()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Configuração de entrega não encontrada."
                        )
                );

        if (configuracao.getTaxaEntrega() == null) {
            throw new IllegalStateException(
                    "Taxa de entrega não configurada."
            );
        }

        if (configuracao.getTaxaEntrega()
                .compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException(
                    "Taxa de entrega inválida."
            );
        }

        return configuracao.getTaxaEntrega();
    }
    public List<PedidoResponse> listarMeus(String authorization) {
        CoreAuthMeResponse autenticado =
                coreClient.buscarAutenticado(authorization);

        if (autenticado == null
                || autenticado.getPessoa() == null
                || autenticado.getPessoa().getId() == null) {
            throw new IllegalStateException(
                    "Não foi possível identificar o cliente autenticado."
            );
        }

        Long clienteId = autenticado.getPessoa().getId();

        return repository.findByClienteIdOrderByDataCriacaoDesc(clienteId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}