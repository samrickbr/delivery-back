package br.com.inova.sigin.delivery.cliente.service;

import br.com.inova.sigin.delivery.cliente.dto.ClienteLoginRequest;
import br.com.inova.sigin.delivery.cliente.dto.ClienteLoginResponse;
import br.com.inova.sigin.delivery.cliente.dto.ClienteRequest;
import br.com.inova.sigin.delivery.cliente.dto.ClienteResponse;
import br.com.inova.sigin.delivery.core.client.CoreClient;
import br.com.inova.sigin.delivery.core.dto.CoreLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final CoreClient coreClient;

    public ClienteResponse criar(ClienteRequest request) {

        var pessoa = coreClient.cadastrarCliente(
                request.getNome(),
                request.getTelefone(),
                request.getCpf(),
                request.getSenha(),
                request.getEmail()
        );

        return ClienteResponse.builder()
                .id(pessoa.getId())
                .nome(pessoa.getNome())
                .cpf(pessoa.getDocumento())
                .telefone(pessoa.getTelefone())
                .email(pessoa.getEmail())
                .build();
    }

    public ClienteLoginResponse login(ClienteLoginRequest request) {

        CoreLoginResponse response = coreClient.autenticarCliente(
                request.getCpf(),
                request.getSenha()
        );

        return ClienteLoginResponse.builder()
                .token(response.getToken())
                .tipo(response.getTipo())
                .clienteId(response.getClienteId())
                .build();
    }
}