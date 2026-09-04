package br.com.inova.sigin.delivery.evento.controller;

import br.com.inova.sigin.delivery.evento.service.EventoProducaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/eventos/producao")
@RequiredArgsConstructor
public class EventoProducaoController {

    private final EventoProducaoService eventoProducaoService;

    @GetMapping
    public SseEmitter conectar() {
        return eventoProducaoService.conectar();
    }
}