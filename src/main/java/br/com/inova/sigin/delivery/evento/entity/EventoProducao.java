package br.com.inova.sigin.delivery.evento.entity;

public record EventoProducao(
        String tipo,
        Long pedidoId,
        String setor,
        Long canalVendaId,
        Object canalVenda,
        String status,
        Long pedidoItemId
) {

    public EventoProducao(
            String tipo,
            Long pedidoId,
            String setor
    ) {
        this(tipo, pedidoId, setor, null, null, null, null);
    }

    public EventoProducao(
            String tipo,
            Long pedidoId,
            String setor,
            Long canalVendaId,
            Object canalVenda,
            String status
    ) {
        this(
                tipo,
                pedidoId,
                setor,
                canalVendaId,
                canalVenda,
                status,
                null
        );
    }
}
