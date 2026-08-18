package br.com.inova.sigin.delivery.core.client;

import br.com.inova.sigin.delivery.core.dto.CatalogoItemResponse;
import br.com.inova.sigin.delivery.core.dto.PessoaRequest;
import br.com.inova.sigin.delivery.core.dto.PessoaResponse;
import br.com.inova.sigin.delivery.core.exception.CoreIntegrationException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class CoreClient {

    private final RestClient restClient;

    public CoreClient(RestClient coreRestClient) {
        this.restClient = coreRestClient;
    }

    public List<CatalogoItemResponse> getCatalogo(Long canalVendaId) {
        try {
            return restClient.get()
                    .uri("/api/catalogo/{canalVendaId}", canalVendaId)
                    .headers(headers -> headers.setBearerAuth(autenticar()))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core rejeitou a requisição. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core apresentou erro interno. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .body(new ParameterizedTypeReference<>() {
                    });

        } catch (CoreIntegrationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CoreIntegrationException(
                    "Não foi possível comunicar com o SIGIN Core.",
                    exception
            );
        }
    }

    public PessoaResponse buscarPessoaPorTelefone(String telefone) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/pessoas/por-telefone")
                            .queryParam("telefone", telefone)
                            .build())
                    .headers(headers -> headers.setBearerAuth(autenticar()))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core rejeitou a busca da pessoa. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core apresentou erro interno ao buscar pessoa. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .body(PessoaResponse.class);

        } catch (CoreIntegrationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CoreIntegrationException(
                    "Não foi possível consultar a pessoa no SIGIN Core.",
                    exception
            );
        }
    }

    public PessoaResponse criarPessoa(PessoaRequest request) {
        try {
            return restClient.post()
                    .uri("/pessoas")
                    .headers(headers -> headers.setBearerAuth(autenticar()))
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (requestHttp, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core rejeitou a criação da pessoa. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (requestHttp, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core apresentou erro interno ao criar pessoa. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .body(PessoaResponse.class);

        } catch (CoreIntegrationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CoreIntegrationException(
                    "Não foi possível criar a pessoa no SIGIN Core.",
                    exception
            );
        }
    }

    private String autenticar() {
        // manter aqui a implementação de autenticação
        // que já foi adicionada durante o P0.3
        return "";
    }
}