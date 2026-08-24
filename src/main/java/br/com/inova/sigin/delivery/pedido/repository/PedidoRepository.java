package br.com.inova.sigin.delivery.pedido.repository;

import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findAllByOrderByDataCriacaoAsc();

    List<Pedido> findByStatusOrderByDataCriacaoAsc(StatusPedido status);

    List<Pedido> findByStatusInOrderByDataCriacaoAsc(List<StatusPedido> status);

    List<Pedido> findByStatusOrderByStatusAlteradoEmAsc(StatusPedido status);

    List<Pedido> findByStatusOrderByStatusAlteradoEmDesc(StatusPedido status);

    List<Pedido> findByStatusInOrderByStatusAlteradoEmAsc(List<StatusPedido> status);

    Optional<Pedido> findByCorePedidoId(Long corePedidoId);
}