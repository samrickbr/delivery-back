package br.com.inova.sigin.delivery.configuracao.dto;

import java.math.BigDecimal;

public record ConfiguracaoSistemaResponse(
        BigDecimal taxaEntregaPadrao
) {
}