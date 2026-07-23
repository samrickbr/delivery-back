package br.com.inova.sigin.delivery.configuracao.repository;

import br.com.inova.sigin.delivery.configuracao.entity.Configuracao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracaoRepository
        extends JpaRepository<Configuracao, Long> {
}