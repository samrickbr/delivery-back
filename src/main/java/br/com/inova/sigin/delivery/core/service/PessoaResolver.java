package br.com.inova.sigin.delivery.core.service;

import br.com.inova.sigin.delivery.core.client.CoreClient;
import br.com.inova.sigin.delivery.core.dto.PessoaRequest;
import br.com.inova.sigin.delivery.core.dto.PessoaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PessoaResolver {

    private final CoreClient coreClient;

    public PessoaResponse resolver(String nome, String telefone) {

        if (telefone != null && !telefone.isBlank()) {
            return coreClient.buscarPessoaPorTelefone(telefone);
        }

        PessoaRequest request = new PessoaRequest();
        request.setNome(nome);
        request.setTelefone(telefone);

        return coreClient.criarPessoa(request);
    }
}