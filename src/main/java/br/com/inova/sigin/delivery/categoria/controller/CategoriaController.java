package br.com.inova.sigin.delivery.categoria.controller;

import br.com.inova.sigin.delivery.categoria.dto.CategoriaRequest;
import br.com.inova.sigin.delivery.categoria.dto.CategoriaResponse;
import br.com.inova.sigin.delivery.categoria.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService service;

    @PostMapping
    public CategoriaResponse salvar(
            @RequestBody CategoriaRequest request) {
        return service.salvar(request);
    }

    @GetMapping
    public List<CategoriaResponse> listar() {
        return service.listar();
    }
}