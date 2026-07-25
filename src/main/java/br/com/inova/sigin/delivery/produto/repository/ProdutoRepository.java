package br.com.inova.sigin.delivery.produto.repository;

import br.com.inova.sigin.delivery.produto.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository
        extends JpaRepository<Produto, Long> {

    List<Produto> findByAtivoTrueAndDisponivelTrue();
}