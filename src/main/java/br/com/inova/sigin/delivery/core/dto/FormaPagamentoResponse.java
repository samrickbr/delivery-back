package br.com.inova.sigin.delivery.core.dto;

public record FormaPagamentoResponse(
        Long id,
        String descricao,
        Boolean ativo,
        Boolean baixaAutomatica
) {
}