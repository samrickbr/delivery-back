package br.com.inova.sigin.delivery.cliente.controller;

import br.com.inova.sigin.delivery.cliente.dto.ClienteLoginRequest;
import br.com.inova.sigin.delivery.cliente.dto.ClienteLoginResponse;
import br.com.inova.sigin.delivery.cliente.dto.ClienteRequest;
import br.com.inova.sigin.delivery.cliente.dto.ClienteResponse;
import br.com.inova.sigin.delivery.cliente.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse criar(
            @RequestBody @Valid ClienteRequest request
    ) {
        return service.criar(request);
    }

    @GetMapping("/cpf/{cpf}")
    public ClienteResponse buscarPorCpf(
            @PathVariable String cpf
    ) {
        return service.buscarPorCpf(cpf);
    }

    @PostMapping("/login")
    public ClienteLoginResponse login(
            @RequestBody @Valid ClienteLoginRequest request
    ) {
        return service.autenticar(request);
    }
}