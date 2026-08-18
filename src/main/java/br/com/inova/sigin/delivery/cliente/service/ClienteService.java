package br.com.inova.sigin.delivery.cliente.service;

import br.com.inova.sigin.delivery.cliente.dto.ClienteLoginRequest;
import br.com.inova.sigin.delivery.cliente.dto.ClienteLoginResponse;
import br.com.inova.sigin.delivery.cliente.dto.ClienteRequest;
import br.com.inova.sigin.delivery.cliente.dto.ClienteResponse;
import br.com.inova.sigin.delivery.core.client.CoreClient;
import br.com.inova.sigin.delivery.core.dto.CoreLoginResponse;
import br.com.inova.sigin.delivery.core.dto.PessoaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final CoreClient coreClient;

    public ClienteResponse criar(ClienteRequest request) {

        PessoaResponse response = coreClient.cadastrarCliente(
                request.getNome(),
                request.getTelefone(),
                request.getCpf(),
                request.getSenha(),
                request.getEmail()
        );

        return ClienteResponse.builder()
                .id(response.getId())
                .nome(response.getNome())
                .cpf(request.getCpf())
                .telefone(response.getTelefone())
                .email(response.getEmail())
                .build();
    }

    public ClienteResponse buscarPorCpf(String cpf) {

        PessoaResponse response = coreClient.buscarPessoaPorDocumento(cpf);

        if (response == null) {
            return null;
        }

        return ClienteResponse.builder()
                .id(response.getId())
                .nome(response.getNome())
                .cpf(response.getDocumento())
                .telefone(response.getTelefone())
                .email(response.getEmail())
                .build();
    }

    public ClienteLoginResponse autenticar(ClienteLoginRequest request) {

        CoreLoginResponse response = coreClient.autenticarCliente(
                request.getCpf(),
                request.getSenha()
        );

        return ClienteLoginResponse.builder()
                .token(response.getToken())
                .tipo(response.getTipo())
                .build();
    }
}