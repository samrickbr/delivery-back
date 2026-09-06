package br.com.inova.sigin.delivery.evento.service;

import br.com.inova.sigin.delivery.evento.entity.EventoProducao;
import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedidoitem.entity.PedidoItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class EventoProducaoService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter conectar() {
        SseEmitter emitter = new SseEmitter(0L);

        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(error -> emitters.remove(emitter));

        try {
            emitter.send(
                    SseEmitter.event()
                            .name("conectado")
                            .data("ok")
            );
        } catch (IOException exception) {
            emitters.remove(emitter);
        }

        return emitter;
    }

    public void novoPedido(Pedido pedido, String setor) {
        EventoProducao evento = new EventoProducao(
                "NOVO_PEDIDO",
                pedido.getId(),
                setor,
                pedido.getCanalVendaId(),
                pedido.getCanalVenda(),
                pedido.getStatus().name()
        );

        enviar("novo-pedido", evento);
    }

    public void novoPedido(Pedido pedido) {
        novoPedido(pedido, null);
    }

    public void pedidoPronto(Pedido pedido, String setor) {
        EventoProducao evento = new EventoProducao(
                "PEDIDO_PRONTO",
                pedido.getId(),
                setor,
                pedido.getCanalVendaId(),
                pedido.getCanalVenda(),
                pedido.getStatus().name()
        );

        enviar("pedido-pronto", evento);
    }

    public void pedidoItemFinalizado(
            Pedido pedido,
            PedidoItem pedidoItem
    ) {
        EventoProducao evento = new EventoProducao(
                "PEDIDO_ITEM_FINALIZADO",
                pedido.getId(),
                pedidoItem.getSetor(),
                null,
                null,
                pedidoItem.getStatusOperacao().name(),
                pedidoItem.getId()
        );

        enviar("pedido-item-finalizado", evento);
    }

    private void enviar(String nome, EventoProducao evento) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name(nome)
                                .data(evento)
                );
            } catch (IOException exception) {
                emitters.remove(emitter);
            }
        }
    }
}
