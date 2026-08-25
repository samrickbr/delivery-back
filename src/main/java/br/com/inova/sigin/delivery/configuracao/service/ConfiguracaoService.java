package br.com.inova.sigin.delivery.configuracao.service;

import br.com.inova.sigin.delivery.configuracao.dto.ConfiguracaoRequest;
import br.com.inova.sigin.delivery.configuracao.dto.ConfiguracaoResponse;
import br.com.inova.sigin.delivery.configuracao.entity.Configuracao;
import br.com.inova.sigin.delivery.configuracao.mapper.ConfiguracaoMapper;
import br.com.inova.sigin.delivery.configuracao.repository.ConfiguracaoRepository;
import br.com.inova.sigin.delivery.core.client.CoreClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ConfiguracaoService {

    private final ConfiguracaoRepository repository;
    private final ConfiguracaoMapper mapper;
    private final CoreClient coreClient;

    public ConfiguracaoResponse salvar(ConfiguracaoRequest request) {

        Configuracao configuracao = repository.findById(1L).orElseGet(Configuracao::new);

        configuracao.setId(1L);
        configuracao.setTaxaEntrega(request.getTaxaEntrega());

        return mapper.toResponse(repository.save(configuracao));
    }

    public ConfiguracaoResponse buscar(Long id) {

        Configuracao configuracao = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Configuração não encontrada."));

        return mapper.toResponse(configuracao);
    }

    public BigDecimal buscarTaxaEntrega() {
        return coreClient.buscarTaxaEntrega();
    }
}