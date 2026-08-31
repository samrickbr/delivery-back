package br.com.inova.sigin.delivery.pedido.service;

import br.com.inova.sigin.delivery.core.client.CoreClient;
import br.com.inova.sigin.delivery.core.config.CoreClientProperties;
import br.com.inova.sigin.delivery.core.dto.CoreAuthMeResponse;
import br.com.inova.sigin.delivery.core.dto.PedidoItemRequest;
import br.com.inova.sigin.delivery.core.dto.PedidoPagamentoRequest;
import br.com.inova.sigin.delivery.pedido.dto.PedidoRequest;
import br.com.inova.sigin.delivery.pedido.dto.PedidoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoCriacaoService {

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

        Long clienteId = request.getClienteId();

        if (clienteId == null) {
            throw new IllegalArgumentException(
                    "Cliente não informado."
            );
        }

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
}