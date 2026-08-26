package br.com.inova.sigin.delivery.pedido.service;

import br.com.inova.sigin.delivery.pedido.dto.CancelamentoItensRequest;
import br.com.inova.sigin.delivery.pedido.dto.CancelamentoRequest;
import br.com.inova.sigin.delivery.pedido.dto.PedidoResponse;
import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import br.com.inova.sigin.delivery.pedido.mapper.PedidoMapper;
import br.com.inova.sigin.delivery.pedido.repository.PedidoRepository;
import br.com.inova.sigin.delivery.pedidohistorico.service.PedidoHistoricoService;
import br.com.inova.sigin.delivery.pedidoitem.entity.PedidoItem;
import br.com.inova.sigin.delivery.pedidoitem.enums.StatusOperacao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PedidoCancelamentoService {

    private final PedidoRepository repository;
    private final PedidoMapper mapper;
    private final PedidoHistoricoService historicoService;

    @Transactional
    public PedidoResponse cancelar(Long id, String setor, CancelamentoRequest request) {
        Pedido pedido = buscarEntidade(id);

        pedido.getItens()
                .stream()
                .filter(item -> getSetor(item).equals(setor))
                .forEach(item -> item.setStatusOperacao(StatusOperacao.CANCELADO));

        boolean todosCancelados = pedido.getItens()
                .stream()
                .allMatch(item -> item.getStatusOperacao() == StatusOperacao.CANCELADO);

        if (todosCancelados) {
            pedido.setStatus(StatusPedido.CANCELADO);
        }

        pedido.setObservacaoOperacao(request.getJustificativa());
        pedido.setStatusAlteradoEm(LocalDateTime.now());

        repository.save(pedido);

        historicoService.registrar(
                pedido,
                null,
                "Sistema",
                setor,
                "SETOR_CANCELADO",
                "Todos os itens do setor foram cancelados. Motivo: " + request.getJustificativa()
        );

        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse cancelarPedido(Long id, CancelamentoRequest request) {
        Pedido pedido = buscarEntidade(id);

        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.setStatusAlteradoEm(LocalDateTime.now());

        historicoService.registrar(
                pedido,
                null,
                "Sistema",
                "BALCAO",
                "PEDIDO_CANCELADO",
                request.getJustificativa()
        );

        return mapper.toResponse(repository.save(pedido));
    }

    @Transactional
    public PedidoResponse cancelarItens(Long id, String setor, CancelamentoItensRequest request) {
        Pedido pedido = buscarEntidade(id);

        for (Long itemId : request.getItens()) {
            PedidoItem item = pedido.getItens()
                    .stream()
                    .filter(i -> i.getId().equals(itemId))
                    .findFirst()
                    .orElseThrow();

            String setorItem = getSetor(item);

            if (!setorItem.equals(setor) && !setor.equals("BALCAO")) {
                throw new RuntimeException("Usuário não pode cancelar este item.");
            }

            item.setStatusOperacao(StatusOperacao.CANCELADO);
            item.setMotivoCancelamento(request.getJustificativa());
            item.setCanceladoEm(LocalDateTime.now());
            item.setCanceladoPor("Sistema");

            historicoService.registrar(
                    pedido,
                    null,
                    "Sistema",
                    setor,
                    "ITEM_CANCELADO",
                    item.getQuantidade() + "x " + item.getProdutoNome() + " - Motivo: " + request.getJustificativa()
            );
        }

        pedido.setStatusAlteradoEm(LocalDateTime.now());
        repository.save(pedido);

        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse cancelarPedidoCompleto(Long id, String justificativa) {
        Pedido pedido = buscarEntidade(id);

        pedido.getItens().forEach(item -> {
            item.setStatusOperacao(StatusOperacao.CANCELADO);
            item.setMotivoCancelamento(justificativa);
            item.setCanceladoEm(LocalDateTime.now());
            item.setCanceladoPor("Sistema");
        });

        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.setStatusAlteradoEm(LocalDateTime.now());

        historicoService.registrar(
                pedido,
                null,
                "Sistema",
                "BALCAO",
                "PEDIDO_CANCELADO",
                justificativa
        );

        return mapper.toResponse(repository.save(pedido));
    }

    private Pedido buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow();
    }

    private String getSetor(PedidoItem item) {
        return item.getSetor();
    }
}