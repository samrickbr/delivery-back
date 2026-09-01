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
        return coreClient.listarMeusEnderecos(authorization)
                .stream()
                .map(ClienteEnderecoResponse::from)
                .toList();
    }

    public ClienteEnderecoResponse criarEndereco(
            String authorization,
            ClienteEnderecoRequest request
    ) {
        return ClienteEnderecoResponse.from(
                coreClient.criarMeuEndereco(
                        request,
                        authorization
                )
        );
    }

    public ClienteEnderecoResponse atualizarEndereco(
            String authorization,
            Long enderecoId,
            ClienteEnderecoRequest request
    ) {
        return ClienteEnderecoResponse.from(
                coreClient.atualizarMeuEndereco(
                        enderecoId,
                        request,
                        authorization
                )
        );
    }

    public ClienteEnderecoResponse definirEnderecoPrincipal(
            String authorization,
            Long enderecoId
    ) {
        return ClienteEnderecoResponse.from(
                coreClient.definirMeuEnderecoPrincipal(
                        enderecoId,
                        authorization
                )
        );
    }

    public void excluirEndereco(
            String authorization,
            Long enderecoId
    ) {
        coreClient.excluirMeuEndereco(
                enderecoId,
                authorization
        );
    }
    public ClienteEnderecoResponse buscarEndereco(
            String authorization,
            Long enderecoId
    ) {
        return ClienteEnderecoResponse.from(
                coreClient.buscarMeuEndereco(
                        enderecoId,
                        authorization
                )
        );
    }

    public List<ClientePesquisaResponse> pesquisarClientes(String busca, String token) {
        return coreClient.pesquisarClientes(busca, token);
    }

    public ClienteResponse cadastrarClienteOperacional(
            ClienteOperacionalRequest request,
            String token
    ) {
        return coreClient.cadastrarClienteOperacional(request, token);
    }

    public ClienteEnderecoResponse cadastrarEnderecoOperacional(
            Long clienteId,
            ClienteEnderecoRequest request,
            String token
    ) {
        return coreClient.cadastrarEnderecoOperacional(clienteId, request, token);
    }

    public List<ClienteEnderecoResponse> listarEnderecosOperacional(Long clienteId, String token) {
        return coreClient.listarEnderecosOperacional(clienteId, token);
    }
}