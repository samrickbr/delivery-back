package br.com.inova.sigin.delivery.core.client;

import br.com.inova.sigin.delivery.core.dto.CatalogoItemResponse;
import br.com.inova.sigin.delivery.core.dto.CoreLoginRequest;
import br.com.inova.sigin.delivery.core.dto.CoreLoginResponse;
import br.com.inova.sigin.delivery.core.dto.PessoaRequest;
import br.com.inova.sigin.delivery.core.dto.PessoaResponse;
import br.com.inova.sigin.delivery.core.exception.CoreIntegrationException;
import br.com.inova.sigin.delivery.core.config.CoreClientProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class CoreClient {

    private final RestClient restClient;
    private final CoreClientProperties properties;

    public CoreClient(RestClient coreRestClient, CoreClientProperties properties) {
        this.restClient = coreRestClient;
        this.properties = properties;
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

    public PessoaResponse buscarPessoaPorDocumento(String documento) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/pessoas/por-documento")
                            .queryParam("documento", documento)
                            .build())
                    .headers(headers -> headers.setBearerAuth(autenticar()))
                    .retrieve()
                    .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(),
                            (request, response) -> {
                            })
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core rejeitou a busca da pessoa por documento. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core apresentou erro interno ao buscar pessoa por documento. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .body(PessoaResponse.class);

        } catch (CoreIntegrationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CoreIntegrationException(
                    "Não foi possível consultar a pessoa por documento no SIGIN Core.",
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
                    .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(),
                            (request, response) -> {
                            })
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
        try {
            CoreLoginRequest request = new CoreLoginRequest(
                    properties.getLogin(),
                    properties.getSenha()
            );

            CoreLoginResponse response = restClient.post()
                    .uri("/auth/login")
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (httpRequest, httpResponse) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core rejeitou a autenticação. HTTP "
                                        + httpResponse.getStatusCode().value()
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (httpRequest, httpResponse) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core apresentou erro interno na autenticação. HTTP "
                                        + httpResponse.getStatusCode().value()
                        );
                    })
                    .body(CoreLoginResponse.class);

            if (response == null || response.getToken() == null || response.getToken().isBlank()) {
                throw new CoreIntegrationException(
                        "SIGIN Core não retornou token de autenticação."
                );
            }

            return response.getToken();

        } catch (CoreIntegrationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CoreIntegrationException(
                    "Não foi possível autenticar no SIGIN Core.",
                    exception
            );
        }
    }
}