package br.com.inova.sigin.delivery.configuracao.mapper;

import br.com.inova.sigin.delivery.configuracao.dto.ConfiguracaoResponse;
import br.com.inova.sigin.delivery.configuracao.entity.Configuracao;
import org.springframework.stereotype.Component;

@Component
public class ConfiguracaoMapper {

    public ConfiguracaoResponse toResponse(Configuracao entity) {

        return ConfiguracaoResponse.builder()
                .id(entity.getId())
                .empresa(entity.getEmpresa())
                .telefone(entity.getTelefone())
                .whatsapp(entity.getWhatsapp())
                .taxaEntrega(entity.getTaxaEntrega())
                .ativo(entity.getAtivo())
                .build();
    }
}