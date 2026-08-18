package br.com.inova.sigin.delivery.produto.service;

import br.com.inova.sigin.delivery.core.client.CoreClient;
import br.com.inova.sigin.delivery.core.config.CoreClientProperties;
import br.com.inova.sigin.delivery.core.dto.CatalogoItemResponse;
import br.com.inova.sigin.delivery.produto.dto.CardapioResponse;
import br.com.inova.sigin.delivery.produto.dto.ProdutoRequest;
import br.com.inova.sigin.delivery.produto.dto.ProdutoResponse;
import br.com.inova.sigin.delivery.produto.entity.Produto;
import br.com.inova.sigin.delivery.produto.mapper.ProdutoMapper;
import br.com.inova.sigin.delivery.produto.repository.ProdutoRepository;
import br.com.inova.sigin.delivery.categoria.entity.Categoria;
import br.com.inova.sigin.delivery.categoria.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository repository;
    private final CategoriaRepository categoriaRepository;
    private final ProdutoMapper mapper;
    private final CoreClient coreClient;
    private final CoreClientProperties coreClientProperties;

    public ProdutoResponse salvar(ProdutoRequest request) {
        Categoria categoria = categoriaRepository.findById(
                request.getCategoriaId()
        ).orElseThrow(() ->
                new RuntimeException("Categoria não encontrada")
        );

        Produto produto = Produto.builder()
                .categoria(categoria)
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .preco(request.getPreco())
                .imagem(request.getImagem())
                .disponivel(true)
                .ativo(true)
                .build();

        return mapper.toResponse(
                repository.save(produto)
        );
    }

    public List<ProdutoResponse> listar() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<CardapioResponse> listarCardapio() {
        return coreClient.getCatalogo(coreClientProperties.getCanalVendaId())
                .stream()
                .map(this::toCardapioResponse)
                .toList();
    }

    private CardapioResponse toCardapioResponse(
            CatalogoItemResponse item) {

        return CardapioResponse.builder()
                .id(item.getProdutoId())
                .nome(item.getProduto())
                .descricao(null)
                .categoria(null)
                .preco(item.getPrecoVenda())
                .imagem(item.getImagem())
                .build();
    }
}