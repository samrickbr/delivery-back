package br.com.inova.sigin.delivery.pedido.repository;

import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import java.util.List;

public interface PedidoRepository
        extends JpaRepository<Pedido, Long> {
    List<Pedido> findByStatusOrderByDataCriacaoAsc(StatusPedido status);

    List<Pedido> findAllByOrderByDataCriacaoAsc();
    List<Pedido> findByStatusInOrderByDataCriacaoAsc(
            List<StatusPedido> status);
    List<Pedido> findByStatusInOrderByIdDesc(List<StatusPedido> status);

}