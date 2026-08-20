package br.com.inova.sigin.delivery.cliente.service;

import br.com.inova.sigin.delivery.cliente.dto.*;
import br.com.inova.sigin.delivery.core.client.CoreClient;
import br.com.inova.sigin.delivery.core.dto.CoreAuthMeResponse;
import br.com.inova.sigin.delivery.core.dto.CoreLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public ClienteResponse buscarAutenticado(String authorization) {

        CoreAuthMeResponse response =
                coreClient.buscarAutenticado(authorization);

        var pessoa = response.getPessoa();

        if (pessoa == null) {
            throw new IllegalStateException(
                    "SIGIN Core não retornou a pessoa do cliente autenticado."
            );
        }

        return ClienteResponse.builder()
                .id(pessoa.getId())
                .nome(pessoa.getNome())
                .cpf(pessoa.getDocumento())
                .telefone(pessoa.getTelefone())
                .email(pessoa.getEmail())
                .build();
    }
    public List<ClienteEnderecoResponse> buscarEnderecos(
            String authorization
    ) {

        CoreAuthMeResponse response =
                coreClient.buscarAutenticado(authorization);

        var pessoa = response.getPessoa();

        if (pessoa == null || pessoa.getId() == null) {
            throw new IllegalStateException(
                    "SIGIN Core não retornou a pessoa do cliente autenticado."
            );
        }

        return coreClient.buscarEnderecos(
                        pessoa.getId(),
                        authorization
                )
                .stream()
                .map(ClienteEnderecoResponse::from)
                .toList();
    }
}