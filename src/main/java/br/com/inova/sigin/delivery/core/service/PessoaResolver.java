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

    public PessoaResponse resolver(String nome, String documento, String telefone) {

        if (documento != null && !documento.isBlank()) {
            PessoaResponse pessoa = coreClient.buscarPessoaPorDocumento(documento);

            if (pessoa != null) {
                return pessoa;
            }
        }

        if (telefone != null && !telefone.isBlank()) {
            PessoaResponse pessoa = coreClient.buscarPessoaPorTelefone(telefone);

            if (pessoa != null) {
                return pessoa;
            }
        }

        PessoaRequest request = new PessoaRequest();
        request.setNome(nome);
        request.setDocumento(documento);
        request.setTelefone(telefone);

        return coreClient.criarPessoa(request);
    }

    public PessoaResponse resolver(String nome, String telefone) {
        return resolver(nome, null, telefone);
    }
}