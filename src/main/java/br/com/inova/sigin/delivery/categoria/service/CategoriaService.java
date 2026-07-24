package br.com.inova.sigin.delivery.categoria.service;

import br.com.inova.sigin.delivery.categoria.dto.CategoriaRequest;
import br.com.inova.sigin.delivery.categoria.dto.CategoriaResponse;
import br.com.inova.sigin.delivery.categoria.entity.Categoria;
import br.com.inova.sigin.delivery.categoria.mapper.CategoriaMapper;
import br.com.inova.sigin.delivery.categoria.repository.CategoriaRepository;
import br.com.inova.sigin.delivery.setor.entity.Setor;
import br.com.inova.sigin.delivery.setor.repository.SetorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;
    private final CategoriaMapper mapper;
    private final SetorRepository setorRepository;

    public CategoriaResponse salvar(CategoriaRequest request) {

        Setor setor = setorRepository.findById(request.getSetorId())
                .orElseThrow(() -> new RuntimeException("Setor não encontrado"));

        Categoria categoria = Categoria.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .setor(setor)
                .ativo(true)
                .build();

        return mapper.toResponse(
                repository.save(categoria)
        );
    }

    public List<CategoriaResponse> listar() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}