package br.com.inova.sigin.delivery.cliente.controller;

import br.com.inova.sigin.delivery.cliente.dto.*;
import br.com.inova.sigin.delivery.cliente.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cliente")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;

    @PostMapping("/cadastro")
    public ClienteResponse cadastrar(
            @RequestBody @Valid ClienteRequest request
    ) {
        return service.criar(request);
    }

    @PostMapping("/login")
    public ClienteLoginResponse login(
            @RequestBody @Valid ClienteLoginRequest request
    ) {
        return service.login(request);
    }

    @GetMapping("/me")
    public ClienteResponse me(
            @RequestHeader("Authorization") String authorization
    ) {
        return service.buscarAutenticado(authorization);
    }

    @GetMapping("/me/enderecos")
    public List<ClienteEnderecoResponse> enderecos(
            @RequestHeader("Authorization") String authorization
    ) {
        return service.buscarEnderecos(authorization);
    }

    @PostMapping("/me/enderecos")
    public ClienteEnderecoResponse criarEndereco(
            @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid ClienteEnderecoRequest request
    ) {
        return service.criarEndereco(
                authorization,
                request
        );
    }

    @GetMapping("/me/enderecos/{enderecoId}")
    public ClienteEnderecoResponse buscarEndereco(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long enderecoId
    ) {
        return service.buscarEndereco(
                authorization,
                enderecoId
        );
    }

    @PutMapping("/me/enderecos/{enderecoId}")
    public ClienteEnderecoResponse atualizarEndereco(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long enderecoId,
            @RequestBody @Valid ClienteEnderecoRequest request
    ) {
        return service.atualizarEndereco(
                authorization,
                enderecoId,
                request
        );
    }

    @DeleteMapping("/me/enderecos/{enderecoId}")
    public void excluirEndereco(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long enderecoId
    ) {
        service.excluirEndereco(
                authorization,
                enderecoId
        );
    }

    @PutMapping("/me/enderecos/{enderecoId}/principal")
    public ClienteEnderecoResponse definirEnderecoPrincipal(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long enderecoId
    ) {
        return service.definirEnderecoPrincipal(
                authorization,
                enderecoId
        );
    }
}