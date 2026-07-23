package br.com.inova.sigin.delivery.produto.repository;

import br.com.inova.sigin.delivery.produto.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository
        extends JpaRepository<Produto, Long> {
}