package br.com.inova.sigin.delivery.pedido.service;

import br.com.inova.sigin.delivery.pedido.dto.*;
import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import br.com.inova.sigin.delivery.pedido.mapper.PedidoMapper;
import br.com.inova.sigin.delivery.pedido.repository.PedidoRepository;
import br.com.inova.sigin.delivery.pedidoitem.entity.PedidoItem;
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
            PedidoPendenciaRequest request) {
        Pedido pedido = buscarEntidade(id);
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setStatusAlteradoEm(LocalDateTime.now());
        pedido.setObservacaoOperacao(
                request.getMotivo()
        );
        return mapper.toResponse(
                repository.save(pedido)
        );
    }

    public PedidoResponse iniciarProducao(Long id) {
        Pedido pedido = buscarEntidade(id);
        statusService.iniciarProducao(pedido);
        pedido.setStatusAlteradoEm(LocalDateTime.now());
        return salvar(pedido);
    }

    public PedidoResponse finalizar(Long id) {
        Pedido pedido = buscarEntidade(id);
        statusService.finalizar(pedido);
        pedido.setStatusAlteradoEm(LocalDateTime.now());
        return salvar(pedido);
    }

    public PedidoResponse cancelar(Long id, CancelamentoRequest request) {
        Pedido pedido = buscarEntidade(id);
        statusService.cancelar(pedido);
        pedido.setObservacaoOperacao(request.getJustificativa());
        pedido.setStatusAlteradoEm(LocalDateTime.now());
        return salvar(pedido);
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
        return repository.findByStatusInOrderByStatusAlteradoEmAsc(
                        List.of(
                                StatusPedido.APROVADO,
                                StatusPedido.EM_PRODUCAO,
                                StatusPedido.PENDENTE
                        )
                )
                .stream()
                .map(pedido -> mapper.toOperacaoResponse(pedido, setor))
                .filter(pedido -> !pedido.getItens().isEmpty())
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
                                StatusPedido.FINALIZADO,
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
    public List<PedidoOperacaoResponse> listarEntregaOperacao() {
        return repository
                .findByStatusInOrderByStatusAlteradoEmAsc(
                        List.of(
                                StatusPedido.FINALIZADO,
                                StatusPedido.SAIU_ENTREGA
                        )
                )
                .stream()
                .map(pedido -> mapper.toOperacaoResponse(pedido, null))
                .toList();
    }

    public List<PedidoBalcaoResponse> listarBalcao() {
        return repository
                .findByStatusOrderByDataCriacaoAsc(StatusPedido.RECEBIDO)
                .stream()
                .map(mapper::toBalcaoResponse)
                .toList();
    }
}