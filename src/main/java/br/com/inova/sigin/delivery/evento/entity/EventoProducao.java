package br.com.inova.sigin.delivery.evento.entity;

public record EventoProducao(
        String tipo,
        Long pedidoId,
        String setor
) {
}
