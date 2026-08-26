package br.com.inova.sigin.delivery.operacional.controller;

import br.com.inova.sigin.delivery.core.client.CoreClient;
import br.com.inova.sigin.delivery.core.dto.CoreAuthMeResponse;
import br.com.inova.sigin.delivery.core.dto.CoreLoginRequest;
import br.com.inova.sigin.delivery.core.dto.CoreLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operacional")
@RequiredArgsConstructor
public class OperacionalController {

    private final CoreClient coreClient;

    @PostMapping("/login")
    public CoreLoginResponse login(
            @RequestBody CoreLoginRequest request
    ) {
        return coreClient.autenticarOperador(
                request.getLogin(),
                request.getSenha()
        );
    }

    @GetMapping("/me")
    public CoreAuthMeResponse me(
            @RequestHeader("Authorization") String authorization
    ) {
        return coreClient.buscarAutenticado(authorization);
    }
}