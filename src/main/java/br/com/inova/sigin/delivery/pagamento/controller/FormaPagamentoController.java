package br.com.inova.sigin.delivery.pagamento.controller;

import br.com.inova.sigin.delivery.core.dto.FormaPagamentoResponse;
import br.com.inova.sigin.delivery.pagamento.service.FormaPagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/formas-pagamento")
@RequiredArgsConstructor
public class FormaPagamentoController {

    private final FormaPagamentoService service;

    @GetMapping
    public List<FormaPagamentoResponse> listar() {
        return service.listar();
    }
}