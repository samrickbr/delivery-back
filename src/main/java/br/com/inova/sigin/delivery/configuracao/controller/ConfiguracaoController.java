package br.com.inova.sigin.delivery.configuracao.controller;

import br.com.inova.sigin.delivery.configuracao.dto.ConfiguracaoRequest;
import br.com.inova.sigin.delivery.configuracao.dto.ConfiguracaoResponse;
import br.com.inova.sigin.delivery.configuracao.service.ConfiguracaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/configuracoes")
@RequiredArgsConstructor
public class ConfiguracaoController {

    private final ConfiguracaoService service;

    @PostMapping
    public ConfiguracaoResponse salvar(
            @RequestBody @Valid ConfiguracaoRequest request) {

        return service.salvar(request);
    }

    @GetMapping("/{id}")
    public ConfiguracaoResponse buscar(
            @PathVariable Long id) {

        return service.buscar(id);
    }
}