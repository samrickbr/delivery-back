package br.com.inova.sigin.delivery.cliente.controller;

import br.com.inova.sigin.delivery.cliente.dto.*;
import br.com.inova.sigin.delivery.cliente.service.ClienteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @GetMapping
    public ResponseEntity<List<ClientePesquisaResponse>> pesquisar(
            @RequestParam String busca,
            HttpServletRequest request
    ) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        return ResponseEntity.ok(service.pesquisarClientes(busca, token));
    }

    @PostMapping("/operacional")
    public ResponseEntity<ClienteResponse> cadastrarOperacional(
            @Valid @RequestBody ClienteOperacionalRequest request,
            HttpServletRequest httpRequest
    ) {
        String token = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.cadastrarClienteOperacional(request, token));
    }

    @PostMapping("/{clienteId}/enderecos")
    public ResponseEntity<ClienteEnderecoResponse> cadastrarEnderecoOperacional(
            @PathVariable Long clienteId,
            @Valid @RequestBody ClienteEnderecoRequest request,
            HttpServletRequest httpRequest
    ) {
        String token = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.cadastrarEnderecoOperacional(
                        clienteId,
                        request,
                        token
                ));
    }
}