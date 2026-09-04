package br.com.inova.sigin.delivery.evento.service;

import br.com.inova.sigin.delivery.evento.entity.EventoProducao;
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

    public void novoPedido(Long pedidoId, String setor) {
        EventoProducao evento = new EventoProducao(
                "NOVO_PEDIDO",
                pedidoId,
                setor
        );

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("novo-pedido")
                                .data(evento)
                );
            } catch (IOException exception) {
                emitters.remove(emitter);
            }
        }
    }
}
