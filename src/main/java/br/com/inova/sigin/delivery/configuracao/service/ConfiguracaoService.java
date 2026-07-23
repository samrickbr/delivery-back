package br.com.inova.sigin.delivery.configuracao.service;

import br.com.inova.sigin.delivery.configuracao.dto.ConfiguracaoRequest;
import br.com.inova.sigin.delivery.configuracao.dto.ConfiguracaoResponse;
import br.com.inova.sigin.delivery.configuracao.entity.Configuracao;
import br.com.inova.sigin.delivery.configuracao.mapper.ConfiguracaoMapper;
import br.com.inova.sigin.delivery.configuracao.repository.ConfiguracaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfiguracaoService {

    private final ConfiguracaoRepository repository;
    private final ConfiguracaoMapper mapper;

    public ConfiguracaoResponse salvar(ConfiguracaoRequest request) {

        Configuracao configuracao = Configuracao.builder()
                .empresa(request.getEmpresa())
                .telefone(request.getTelefone())
                .whatsapp(request.getWhatsapp())
                .taxaEntrega(request.getTaxaEntrega())
                .ativo(true)
                .build();

        return mapper.toResponse(
                repository.save(configuracao)
        );
    }

    public ConfiguracaoResponse buscar(Long id) {

        return mapper.toResponse(
                repository.findById(id).orElseThrow()
        );
    }
}