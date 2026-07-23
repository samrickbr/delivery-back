package br.com.inova.sigin.delivery.pedido.service;

import br.com.inova.sigin.delivery.pedido.dto.PedidoItemRequest;
import br.com.inova.sigin.delivery.pedido.dto.PedidoPendenciaRequest;
import br.com.inova.sigin.delivery.pedido.dto.PedidoRequest;
import br.com.inova.sigin.delivery.pedido.dto.PedidoResponse;
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
        return salvar(pedido);
    }

    @Transactional
    public PedidoResponse colocarPendente(
            Long id,
            PedidoPendenciaRequest request) {
        Pedido pedido = buscarEntidade(id);
        pedido.setStatus(StatusPedido.PENDENTE);
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
        return salvar(pedido);
    }

    public PedidoResponse sairEntrega(Long id) {
        Pedido pedido = buscarEntidade(id);
        statusService.sairEntrega(pedido);
        return salvar(pedido);
    }

    public PedidoResponse finalizar(Long id) {
        Pedido pedido = buscarEntidade(id);
        statusService.finalizar(pedido);
        return salvar(pedido);
    }

    public PedidoResponse cancelar(Long id) {
        Pedido pedido = buscarEntidade(id);
        statusService.cancelar(pedido);
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

    public List<PedidoResponse> pedidosCozinha() {
        return repository.findByStatusInOrderByDataCriacaoAsc(
                        List.of(
                                StatusPedido.APROVADO,
                                StatusPedido.EM_PRODUCAO,
                                StatusPedido.PENDENTE
                        )
                )
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
    public List<PedidoResponse> listarFinalizados() {
        return repository
                .findByStatusInOrderByIdDesc(
                        List.of(
                                StatusPedido.FINALIZADO,
                                StatusPedido.SAIU_ENTREGA,
                                StatusPedido.CANCELADO
                        )
                )
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}