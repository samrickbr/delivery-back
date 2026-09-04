package br.com.inova.sigin.delivery.pedido.service;

import br.com.inova.sigin.delivery.core.client.CoreClient;
import br.com.inova.sigin.delivery.core.dto.CoreAuthMeResponse;
import br.com.inova.sigin.delivery.evento.service.EventoProducaoService;
import br.com.inova.sigin.delivery.pedido.dto.PedidoPendenciaRequest;
import br.com.inova.sigin.delivery.pedido.dto.PedidoResponse;
import br.com.inova.sigin.delivery.pedido.dto.SeparacaoItemRequest;
import br.com.inova.sigin.delivery.pedido.dto.SeparacaoRequest;
import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import br.com.inova.sigin.delivery.pedido.mapper.PedidoMapper;
import br.com.inova.sigin.delivery.pedido.repository.PedidoRepository;
import br.com.inova.sigin.delivery.pedidohistorico.service.PedidoHistoricoService;
import br.com.inova.sigin.delivery.pedidoitem.entity.PedidoItem;
import br.com.inova.sigin.delivery.pedidoitem.enums.StatusOperacao;
import br.com.inova.sigin.delivery.pedidoitem.repository.PedidoItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PedidoOperacaoService {

    private final PedidoRepository repository;
    private final PedidoItemRepository itemRepository;
    private final PedidoMapper mapper;
    private final StatusPedidoService statusService;
    private final PedidoHistoricoService historicoService;
    private final CoreClient coreClient;
        private final EventoProducaoService eventoProducaoService;

    @Transactional
    public PedidoResponse aprovar(
            Long id,
            String authorization
    ) {
        Pedido pedido = buscarEntidade(id);

        Long usuarioId = buscarUsuarioId(authorization);

        statusService.aprovar(pedido);

        pedido.getItens()
                .stream()
                .filter(item ->
                        item.getStatusOperacao() != StatusOperacao.CANCELADO
                )
                .forEach(item ->
                        item.setStatusOperacao(StatusOperacao.APROVADO)
                );

        pedido.setStatusAlteradoEm(LocalDateTime.now());

        historicoService.registrar(
                pedido,
                usuarioId,
                "Sistema",
                "BALCAO",
                "APROVADO",
                "Pedido aprovado."
        );

        PedidoResponse response = mapper.toResponse(repository.save(pedido));

        var setores = response.getItens()
                .stream()
                .filter(item -> item.getSetor() != null)
                .filter(item -> !"CANCELADO".equalsIgnoreCase(item.getStatusOperacao()))
                .map(item -> item.getSetor().trim().toUpperCase())
                .filter(setor ->
                        "COZINHA".equals(setor)
                                || "PIZZARIA".equals(setor)
                )
                .distinct()
                .toList();

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        setores.forEach(setor ->
                                eventoProducaoService.novoPedido(
                                        response.getId(),
                                        setor
                                )
                        );
                    }
                }
        );

        return response;
    }

    @Transactional
    public PedidoResponse colocarPendente(
            Long id,
            String setor,
            PedidoPendenciaRequest request,
            String authorization
    ) {
        Pedido pedido = buscarEntidade(id);

        Long usuarioId = buscarUsuarioId(authorization);

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
                usuarioId,
                "Sistema",
                setor,
                "PENDENTE",
                request.getMotivo()
        );

        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse iniciarProducao(
            Long id,
            String setor,
            String authorization
    ) {
        Pedido pedido = buscarEntidade(id);

        Long usuarioId = buscarUsuarioId(authorization);

        pedido.getItens()
                .stream()
                .filter(item ->
                        getSetor(item).equals(setor)
                                && item.getStatusOperacao() != StatusOperacao.CANCELADO
                )
                .forEach(item ->
                        item.setStatusOperacao(StatusOperacao.EM_PRODUCAO)
                );

        pedido.setStatusAlteradoEm(LocalDateTime.now());

        historicoService.registrar(
                pedido,
                usuarioId,
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
            String setor,
            String authorization
    ) {
        Pedido pedido = buscarEntidade(id);

        Long usuarioId = buscarUsuarioId(authorization);

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

        pedido.setStatusAlteradoEm(LocalDateTime.now());

        repository.save(pedido);

        historicoService.registrar(
                pedido,
                usuarioId,
                "Sistema",
                setor,
                "FINALIZADO",
                "Setor finalizou a produção."
        );

        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse conferir(
            Long id,
            String authorization
    ) {
        Pedido pedido = buscarEntidade(id);

        Long usuarioId = buscarUsuarioId(authorization);

        LocalDateTime agora = LocalDateTime.now();

        pedido.setStatus(StatusPedido.AGUARDANDO_SEPARACAO);
        pedido.setConferenciaEm(agora);
        pedido.setConferenciaPorUsuarioId(usuarioId);
        pedido.setStatusAlteradoEm(agora);

        repository.save(pedido);

        historicoService.registrar(
                pedido,
                usuarioId,
                "Sistema",
                "BALCAO",
                "CONFERENCIA",
                "Conferência realizada."
        );

        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse separar(
            Long id,
            String authorization
    ) {
        Pedido pedido = buscarEntidade(id);

        Long usuarioId = buscarUsuarioId(authorization);

        pedido.setStatus(StatusPedido.SEPARADO);
        pedido.setStatusAlteradoEm(LocalDateTime.now());

        repository.save(pedido);

        historicoService.registrar(
                pedido,
                usuarioId,
                "Sistema",
                "BALCAO",
                "SEPARACAO",
                "Pedido separado."
        );

        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse liberarEntrega(
            Long id,
            SeparacaoRequest request,
            String authorization
    ) {
        Pedido pedido = buscarEntidade(id);

        Long usuarioId = buscarUsuarioId(authorization);

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
            throw new IllegalArgumentException(
                    "Existem itens sem separação."
            );
        }

        pedido.setStatus(StatusPedido.SEPARADO);
        pedido.setStatusAlteradoEm(LocalDateTime.now());

        repository.save(pedido);

        historicoService.registrar(
                pedido,
                usuarioId,
                "Sistema",
                "BALCAO",
                "SEPARACAO",
                "Pedido separado e liberado para entrega."
        );

        return mapper.toResponse(pedido);
    }

    private Pedido buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow();
    }

    private String getSetor(PedidoItem item) {
        return item.getSetor();
    }

    private Long buscarUsuarioId(String authorization) {

        CoreAuthMeResponse autenticado =
                coreClient.buscarAutenticado(authorization);

        if (autenticado == null || autenticado.getId() == null) {
            throw new IllegalStateException(
                    "Não foi possível identificar o usuário autenticado."
            );
        }

        if (autenticado.getPessoa() == null
                || autenticado.getPessoa().getId() == null) {
            throw new IllegalStateException(
                    "Usuário autenticado não possui pessoa vinculada."
            );
        }

        return autenticado.getId();
    }
}