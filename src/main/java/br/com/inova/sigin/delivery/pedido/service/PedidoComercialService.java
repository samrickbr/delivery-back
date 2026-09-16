package br.com.inova.sigin.delivery.pedido.service;

import br.com.inova.sigin.delivery.core.client.CoreClient;
import br.com.inova.sigin.delivery.core.dto.PessoaResponse;
import br.com.inova.sigin.delivery.pedido.dto.PedidoItemRequest;
import br.com.inova.sigin.delivery.pedido.dto.PedidoPagamentoRequest;
import br.com.inova.sigin.delivery.pedido.dto.PedidoResponse;
import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import br.com.inova.sigin.delivery.pedido.exception.ProducoesPendentesException;
import br.com.inova.sigin.delivery.pedido.mapper.PedidoMapper;
import br.com.inova.sigin.delivery.pedido.repository.PedidoRepository;
import br.com.inova.sigin.delivery.pedidoitem.entity.PedidoItem;
import br.com.inova.sigin.delivery.pedidoitem.enums.StatusOperacao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PedidoComercialService {

    private final PedidoRepository repository;
    private final PedidoMapper mapper;
    private final CoreClient coreClient;
    private final PedidoProjecaoService pedidoProjecaoService;

    @Transactional
    public PedidoResponse buscarPorId(Long pedidoId) {
        Pedido pedido = buscarEntidade(pedidoId);

        br.com.inova.sigin.delivery.core.dto.PedidoResponse coreResponse =
                coreClient.buscarPedido(
                        pedido.getCorePedidoId()
                );

        return sincronizar(pedido, coreResponse);
    }

    @Transactional
    public PedidoResponse adicionarItem(
            Long pedidoId,
            PedidoItemRequest request
    ) {
        Pedido pedido = buscarEntidade(pedidoId);

        if (request.getQuantidade() == null
                || request.getQuantidade() <= 0) {
            throw new IllegalArgumentException(
                    "Quantidade deve ser maior que zero."
            );
        }

        if (request.getItemId() != null) {
            PedidoItem item = buscarItemDoPedido(
                    pedido,
                    request.getItemId()
            );

            validarItemEditavel(item);

            if (item.getQuantidade() == null) {
                throw new IllegalStateException(
                        "Item sem quantidade definida."
                );
            }

            try {
                request.setQuantidade(
                        Math.addExact(
                                item.getQuantidade(),
                                request.getQuantidade()
                        )
                );
            } catch (ArithmeticException exception) {
                throw new IllegalArgumentException(
                        "Quantidade excede o limite permitido.",
                        exception
                );
            }

            return alterarQuantidadeItem(
                    pedidoId,
                    item.getId(),
                    request
            );
        }

        if (request.getProdutoId() == null) {
            throw new IllegalArgumentException(
                    "Produto é obrigatório."
            );
        }

        br.com.inova.sigin.delivery.core.dto.PedidoResponse coreResponse =
                coreClient.adicionarItem(
                        pedido.getCorePedidoId(),
                        request
                );

        return sincronizar(pedido, coreResponse);
    }

    @Transactional
    public PedidoResponse alterarQuantidadeItem(
            Long pedidoId,
            Long itemId,
            PedidoItemRequest request
    ) {
        Pedido pedido = buscarEntidade(pedidoId);

        PedidoItem item = buscarItemDoPedido(
                pedido,
                itemId
        );

        validarItemEditavel(item);

        if (request.getQuantidade() == null
                || request.getQuantidade() <= 0) {
            throw new IllegalArgumentException(
                    "Quantidade deve ser maior que zero."
            );
        }

        Long coreItemId = obterCoreItemId(item);

        br.com.inova.sigin.delivery.core.dto.PedidoResponse coreResponse =
                coreClient.alterarQuantidadeItem(
                        pedido.getCorePedidoId(),
                        coreItemId,
                        java.math.BigDecimal.valueOf(
                                request.getQuantidade()
                        )
                );

        return sincronizar(pedido, coreResponse);
    }

    @Transactional
    public void removerItem(
            Long pedidoId,
            Long itemId
    ) {
        Pedido pedido = buscarEntidade(pedidoId);

        PedidoItem item = buscarItemDoPedido(
                pedido,
                itemId
        );

        validarItemEditavel(item);

        Long coreItemId = obterCoreItemId(item);

        coreClient.removerItem(
                pedido.getCorePedidoId(),
                coreItemId
        );

        br.com.inova.sigin.delivery.core.dto.PedidoResponse coreResponse =
                coreClient.buscarPedido(
                        pedido.getCorePedidoId()
                );

        sincronizar(pedido, coreResponse);
    }

    @Transactional
    public PedidoResponse adicionarPagamento(
            Long pedidoId,
            PedidoPagamentoRequest request
    ) {
        Pedido pedido = buscarEntidade(pedidoId);

        br.com.inova.sigin.delivery.core.dto.PedidoResponse coreResponse =
                coreClient.adicionarPagamento(
                        pedido.getCorePedidoId(),
                        request
                );

        return sincronizar(pedido, coreResponse);
    }

    @Transactional
    public PedidoResponse alterarPagamento(
            Long pedidoId,
            Long pagamentoId,
            PedidoPagamentoRequest request
    ) {
        Pedido pedido = buscarEntidade(pedidoId);

        br.com.inova.sigin.delivery.core.dto.PedidoPagamentoRequest coreRequest =
                new br.com.inova.sigin.delivery.core.dto.PedidoPagamentoRequest(
                        request.getFormaPagamentoId(),
                        request.getValor()
                );

        br.com.inova.sigin.delivery.core.dto.PedidoResponse coreResponse =
                coreClient.alterarPagamento(
                        pedido.getCorePedidoId(),
                        pagamentoId,
                        coreRequest
                );

        return sincronizar(pedido, coreResponse);
    }

    @Transactional
    public PedidoResponse faturar(Long pedidoId) {
        Pedido pedido = buscarEntidade(pedidoId);

        validarStatusPermitidoParaFaturamento(pedido);

        concluirItensDeBalcao(pedido);
        validarItensParaFaturamento(pedido);

        br.com.inova.sigin.delivery.core.dto.PedidoResponse coreResponse =
                coreClient.faturarPedido(
                        pedido.getCorePedidoId()
                );

        pedido.setStatus(br.com.inova.sigin.delivery.pedido.enums.StatusPedido.FINALIZADO);
        pedido.setStatusAlteradoEm(LocalDateTime.now());
        repository.save(pedido);

        return sincronizar(pedido, coreResponse);
    }
    private void validarStatusPermitidoParaFaturamento(Pedido pedido) {
        StatusPedido status = pedido.getStatus();

        PessoaResponse consumidorFinal = coreClient.buscarConsumidorFinal();

        boolean ehConsumidorFinal =
                pedido.getClienteId() != null
                        && pedido.getClienteId().equals(consumidorFinal.getId());

        boolean excecaoBalcaoRetirada =
                status == StatusPedido.FINALIZADO
                        && ehConsumidorFinal
                        && "RETIRADA".equalsIgnoreCase(
                        pedido.getTipoRecebimento()
                )
                        && pedido.getItens()
                        .stream()
                        .filter(item -> item.getStatusOperacao() != StatusOperacao.CANCELADO)
                        .allMatch(item ->
                                "BALCAO".equalsIgnoreCase(item.getSetor())
                        );

        boolean pedidoEntregue =
                status == StatusPedido.ENTREGUE;

        if (!excecaoBalcaoRetirada && !pedidoEntregue) {
            throw new IllegalArgumentException(
                    "Pedido não está em uma situação permitida para faturamento."
            );
        }
    }
    private void concluirItensDeBalcao(Pedido pedido) {
        boolean alterou = false;

        for (PedidoItem item : pedido.getItens()) {
            if (item == null) {
                continue;
            }

            String setor = normalizar(item.getSetor());

            if (!"BALCAO".equals(setor)) {
                continue;
            }

            if (item.getStatusOperacao() == null) {
                throw new IllegalArgumentException(
                        "Item do Balcão sem status operacional."
                );
            }

            if (item.getStatusOperacao() != StatusOperacao.CANCELADO
                    && item.getStatusOperacao() != StatusOperacao.FINALIZADO) {

                item.setStatusOperacao(StatusOperacao.FINALIZADO);
                alterou = true;
            }
        }

        if (alterou) {
            pedido.setStatusAlteradoEm(LocalDateTime.now());
            repository.save(pedido);
        }
    }

    private void validarItensParaFaturamento(Pedido pedido) {
        List<PedidoItem> bloqueados = new ArrayList<>();

        for (PedidoItem item : pedido.getItens()) {
            if (item == null) {
                continue;
            }

            String setor = normalizar(item.getSetor());
            StatusOperacao status = item.getStatusOperacao();

            if (status == null) {
                bloqueados.add(item);
                continue;
            }

            if ("BALCAO".equals(setor)) {
                continue;
            }

            if ("COZINHA".equals(setor)
                    || "PIZZARIA".equals(setor)) {

                if (status != StatusOperacao.FINALIZADO
                        && status != StatusOperacao.CANCELADO) {

                    bloqueados.add(item);
                }
            }
        }

        if (!bloqueados.isEmpty()) {
            throw new ProducoesPendentesException(bloqueados);
        }
    }

    private PedidoResponse sincronizar(
            Pedido pedido,
            br.com.inova.sigin.delivery.core.dto.PedidoResponse coreResponse
    ) {
        return pedidoProjecaoService.projetar(
                coreResponse,
                pedido.getClienteWhatsapp()
        );
    }

    private Pedido buscarEntidade(Long pedidoId) {
        return repository.findById(pedidoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Pedido não encontrado."
                        )
                );
    }

    private void validarItemEditavel(PedidoItem item) {
        if (item.getStatusOperacao() == null) {
            throw new IllegalStateException(
                    "Item sem status operacional definido."
            );
        }

        switch (item.getStatusOperacao()) {
            case CANCELADO -> throw new IllegalArgumentException(
                    "Item cancelado não pode ser alterado."
            );

            case EM_PRODUCAO, FINALIZADO -> throw new IllegalArgumentException(
                    "Item não pode ser alterado após o início da produção."
            );

            default -> {
                // Item ainda pode sofrer alteração comercial.
            }
        }
    }

    private PedidoItem buscarItemDoPedido(
            Pedido pedido,
            Long itemId
    ) {
        return pedido.getItens()
                .stream()
                .filter(item ->
                        item.getId().equals(itemId)
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Item não pertence ao pedido informado."
                        )
                );
    }

    private Long obterCoreItemId(PedidoItem item) {
        if (item.getCoreItemId() == null) {
            throw new IllegalStateException(
                    "Item sem referência ao item correspondente no SIGIN Core."
            );
        }

        return item.getCoreItemId();
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }

        return valor
                .trim()
                .toUpperCase(Locale.ROOT);
    }
}
