package br.com.inova.sigin.delivery.categoria.mapper;

import br.com.inova.sigin.delivery.categoria.dto.CategoriaResponse;
import br.com.inova.sigin.delivery.categoria.entity.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public CategoriaResponse toResponse(Categoria categoria) {

        return CategoriaResponse.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .descricao(categoria.getDescricao())
                .ativo(categoria.getAtivo())
                .build();
    }
}