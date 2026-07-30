package br.com.inova.sigin.delivery.pedidohistorico.repository;

import br.com.inova.sigin.delivery.pedidohistorico.entity.PedidoHistorico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoHistoricoRepository
        extends JpaRepository<PedidoHistorico, Long> {

    List<PedidoHistorico> findByPedidoIdOrderByDataHoraAsc(Long pedidoId);

}