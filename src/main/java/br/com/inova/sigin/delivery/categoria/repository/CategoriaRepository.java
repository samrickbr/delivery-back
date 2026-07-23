package br.com.inova.sigin.delivery.categoria.repository;

import br.com.inova.sigin.delivery.categoria.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository
        extends JpaRepository<Categoria, Long> {
}