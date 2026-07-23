package br.com.inova.sigin.delivery.produto.controller;

import br.com.inova.sigin.delivery.produto.dto.ProdutoRequest;
import br.com.inova.sigin.delivery.produto.dto.ProdutoResponse;
import br.com.inova.sigin.delivery.produto.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService service;


    @PostMapping
    public ProdutoResponse salvar(
            @RequestBody ProdutoRequest request) {

        return service.salvar(request);
    }


    @GetMapping
    public List<ProdutoResponse> listar() {

        return service.listar();
    }
}