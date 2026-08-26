package br.com.inova.sigin.delivery.pedido.service;

import br.com.inova.sigin.delivery.pedido.dto.PedidoResponse;
import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import br.com.inova.sigin.delivery.pedido.mapper.PedidoMapper;
import br.com.inova.sigin.delivery.pedido.repository.PedidoRepository;
import br.com.inova.sigin.delivery.pedidohistorico.service.PedidoHistoricoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PedidoEntregaService {

    private final PedidoRepository repository;
    private final PedidoMapper mapper;
    private final PedidoHistoricoService historicoService;

    public PedidoResponse sairParaEntrega(Long id) {
        Pedido pedido = buscarEntidade(id);

        if ("RETIRADA".equalsIgnoreCase(pedido.getTipoRecebimento())) {
            throw new IllegalStateException("Pedido de retirada não pode sair para entrega.");
        }

        pedido.setStatus(StatusPedido.SAIU_ENTREGA);
        pedido.setStatusAlteradoEm(LocalDateTime.now());

        return mapper.toResponse(repository.save(pedido));
    }

    public PedidoResponse entregar(Long id) {
        Pedido pedido = buscarEntidade(id);

        pedido.setStatus(StatusPedido.ENTREGUE);
        pedido.setStatusAlteradoEm(LocalDateTime.now());

        repository.save(pedido);

        historicoService.registrar(
                pedido,
                null,
                "Sistema",
                "ENTREGA",
                "SAIU_ENTREGA",
                "Pedido saiu para entrega."
        );

        return mapper.toResponse(pedido);
    }

    private Pedido buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow();
    }
}