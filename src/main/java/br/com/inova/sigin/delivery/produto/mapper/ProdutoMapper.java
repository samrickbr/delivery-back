package br.com.inova.sigin.delivery.produto.mapper;

import br.com.inova.sigin.delivery.produto.dto.ProdutoResponse;
import br.com.inova.sigin.delivery.produto.entity.Produto;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {

    public ProdutoResponse toResponse(Produto produto) {

        return ProdutoResponse.builder()
                .id(produto.getId())
                .categoriaId(produto.getCategoria().getId())
                .categoria(produto.getCategoria().getNome())
                .nome(produto.getNome())
                .descricao(produto.getDescricao())
                .preco(produto.getPreco())
                .imagem(produto.getImagem())
                .disponivel(produto.getDisponivel())
                .ativo(produto.getAtivo())
                .build();
    }
}