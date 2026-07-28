package br.com.inova.sigin.delivery.pedido.service;

import br.com.inova.sigin.delivery.pedido.dto.*;
import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import br.com.inova.sigin.delivery.pedido.mapper.PedidoMapper;
import br.com.inova.sigin.delivery.pedido.repository.PedidoRepository;
import br.com.inova.sigin.delivery.pedidoitem.entity.PedidoItem;
import br.com.inova.sigin.delivery.pedidoitem.enums.StatusOperacao;
import br.com.inova.sigin.delivery.pedidoitem.repository.PedidoItemRepository;
import br.com.inova.sigin.delivery.produto.entity.Produto;
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


    public PedidoResponse criar(PedidoRequest request) {

        Pedido pedido = Pedido.builder()
                .clienteNome(request.getClienteNome())
                .clienteWhatsapp(request.getClienteWhatsapp())
                .status(StatusPedido.RECEBIDO)
                .valorProdutos(BigDecimal.ZERO)
                .taxaEntrega(BigDecimal.ZERO)
                .valorTotal(BigDecimal.ZERO)
                .observacao(request.getObservacao())
                .dataCriacao(LocalDateTime.now())
                .statusAlteradoEm(LocalDateTime.now())
                .build();

        pedido = repository.save(pedido);

        BigDecimal total = BigDecimal.ZERO;

        for (PedidoItemRequest itemRequest : request.getItens()) {

            Produto produto = produtoRepository.findById(
                    itemRequest.getProdutoId()
            ).orElseThrow();

            BigDecimal valorItem =
                    produto.getPreco()
                            .multiply(
                                    BigDecimal.valueOf(
                                            itemRequest.getQuantidade()
                                    )
                            );
            PedidoItem item = PedidoItem.builder()
                    .pedido(pedido)
                    .produto(produto)
                    .quantidade(itemRequest.getQuantidade())
                    .valorUnitario(produto.getPreco())
                    .valorTotal(valorItem)
                    .separado(false)
                    .statusOperacao(StatusOperacao.APROVADO)
                    .build();
            itemRepository.save(item);
            total = total.add(valorItem);
        }
        pedido.setValorProdutos(total);
        pedido.setValorTotal(total);
        pedido.setStatusAlteradoEm(LocalDateTime.now());
        return mapper.toResponse(
                repository.save(pedido)
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
                        item.getProduto()
                                .getCategoria()
                                .getSetor()
                                .getNome()
                                .equals(setor)
                )
                .forEach(item -> {

                    item.setStatusOperacao(
                            StatusOperacao.PENDENTE
                    );

                });


        pedido.setObservacaoOperacao(
                request.getMotivo()
        );

        pedido.setStatusAlteradoEm(
                LocalDateTime.now()
        );
        repository.save(pedido);
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
                        item.getProduto()
                                .getCategoria()
                                .getSetor()
                                .getNome()
                                .equals(setor)
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
                                item.getStatusOperacao()
                                        == StatusOperacao.CANCELADO
                        );

        if (todosCancelados) {

            pedido.setStatus(
                    StatusPedido.CANCELADO
            );

        }

        pedido.setObservacaoOperacao(
                request.getJustificativa()
        );

        pedido.setStatusAlteradoEm(
                LocalDateTime.now()
        );
        repository.save(pedido);
        return mapper.toResponse(pedido);
    }

    public List<PedidoResponse> listar() {
        return repository.findAllByOrderByDataCriacaoAsc()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<PedidoResponse> listarPorStatus(
            StatusPedido status) {
        return repository.findByStatusOrderByDataCriacaoAsc(status)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<PedidoOperacaoResponse> pedidosCozinha(String setor) {

        List<PedidoItem> itens =
                itemRepository.findByProdutoCategoriaSetorNomeAndStatusOperacaoIn(
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

        return mapper.toResponse(
                repository.save(pedido)
        );
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
    public List<PedidoOperacaoResponse> pedidosOperacaoCozinha(){
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

    // =====================================
    // ENTREGA - pedidos finalizados ou em rota
    // =====================================
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
                                StatusPedido.FINALIZADO
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
    public PedidoResponse liberarEntrega(Long id, SeparacaoRequest request) {

        Pedido pedido = buscarEntidade(id);

        for (SeparacaoItemRequest itemRequest : request.getItens()) {

            PedidoItem item = itemRepository.findById(itemRequest.getItemId())
                    .orElseThrow();

            item.setSeparado(itemRequest.getSeparado());

            itemRepository.save(item);
        }

        boolean todosSeparados =
                pedido.getItens()
                        .stream()
                        .allMatch(PedidoItem::getSeparado);

        if (!todosSeparados) {
            throw new RuntimeException("Existem itens sem separação.");
        }

        pedido.setStatus(StatusPedido.SEPARADO);
        pedido.setStatusAlteradoEm(LocalDateTime.now());

        return mapper.toResponse(
                repository.save(pedido)
        );
    }

    @Transactional
    public PedidoResponse iniciarProducao(Long id, String setor) {

        Pedido pedido = buscarEntidade(id);

        pedido.getItens()
                .stream()
                .filter(item ->
                        item.getProduto()
                                .getCategoria()
                                .getSetor()
                                .getNome()
                                .equals(setor)
                )
                .forEach(item ->
                        item.setStatusOperacao(
                                StatusOperacao.EM_PRODUCAO
                        )
                );


        pedido.setStatusAlteradoEm(
                LocalDateTime.now()
        );

        repository.save(pedido);

        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse finalizar(Long id, String setor) {

        Pedido pedido = buscarEntidade(id);

        pedido.getItens()
                .stream()
                .filter(item ->
                        item.getProduto()
                                .getCategoria()
                                .getSetor()
                                .getNome()
                                .equals(setor)
                )
                .forEach(item ->
                        item.setStatusOperacao(
                                StatusOperacao.FINALIZADO
                        )
                );


        boolean todosFinalizados =
                pedido.getItens()
                        .stream()
                        .allMatch(item ->
                                item.getStatusOperacao()
                                        == StatusOperacao.FINALIZADO
                        );


        if (todosFinalizados) {

            pedido.setStatus(
                    StatusPedido.FINALIZADO
            );
        }


        pedido.setStatusAlteradoEm(
                LocalDateTime.now()
        );


        repository.save(pedido);

        return mapper.toResponse(pedido);
    }
}