package br.com.inova.sigin.delivery.pedidoitem.repository;

import br.com.inova.sigin.delivery.pedidoitem.entity.PedidoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoItemRepository
        extends JpaRepository<PedidoItem, Long> {
}