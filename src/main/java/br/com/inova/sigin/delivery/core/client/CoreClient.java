package br.com.inova.sigin.delivery.core.client;

import br.com.inova.sigin.delivery.core.config.CoreClientProperties;
import br.com.inova.sigin.delivery.core.dto.CatalogoItemResponse;
import br.com.inova.sigin.delivery.core.dto.CoreLoginRequest;
import br.com.inova.sigin.delivery.core.dto.CoreLoginResponse;
import br.com.inova.sigin.delivery.core.dto.PessoaRequest;
import br.com.inova.sigin.delivery.core.dto.PessoaResponse;
import br.com.inova.sigin.delivery.core.exception.CoreIntegrationException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CoreClient {

    private final RestClient restClient;
    private final CoreClientProperties properties;

    public CoreClient(
            RestClient coreRestClient,
            CoreClientProperties properties
    ) {
        this.restClient = coreRestClient;
        this.properties = properties;
    }

    public List<CatalogoItemResponse> getCatalogo(Long canalVendaId) {
        try {
            return restClient.get()
                    .uri("/api/catalogo/{canalVendaId}", canalVendaId)
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
                    .onStatus(
                            status -> status.value() == HttpStatus.NOT_FOUND.value(),
                            (request, response) -> {
                            }
                    )
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
                    .onStatus(
                            status -> status.value() == HttpStatus.NOT_FOUND.value(),
                            (request, response) -> {
                            }
                    )
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

    public PessoaResponse cadastrarCliente(
            String nome,
            String telefone,
            String documento,
            String senha,
            String email
    ) {
        try {
            Map<String, Object> request = new HashMap<>();

            request.put("nome", nome);
            request.put("telefone", telefone);
            request.put("documento", documento);
            request.put("senha", senha);

            if (email != null && !email.isBlank()) {
                request.put("email", email);
            }

            return restClient.post()
                    .uri("/api/delivery/clientes")
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (requestHttp, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core rejeitou o cadastro do cliente. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (requestHttp, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core apresentou erro interno ao cadastrar cliente. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .body(PessoaResponse.class);

        } catch (CoreIntegrationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CoreIntegrationException(
                    "Não foi possível cadastrar o cliente no SIGIN Core.",
                    exception
            );
        }
    }

    public CoreLoginResponse autenticarCliente(String cpf, String senha) {
        try {
            if (cpf == null || cpf.isBlank()) {
                throw new CoreIntegrationException("CPF não informado.");
            }

            if (senha == null || senha.isBlank()) {
                throw new CoreIntegrationException("Senha não informada.");
            }

            String login = cpf.replaceAll("\\D", "");

            CoreLoginRequest request = new CoreLoginRequest(
                    login,
                    senha
            );

            CoreLoginResponse response = restClient.post()
                    .uri("/auth/login")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (httpRequest, httpResponse) -> {
                        throw new CoreIntegrationException(
                                extrairMensagemErro(
                                        httpResponse.getBody() != null
                                                ? new String(httpResponse.getBody().readAllBytes())
                                                : null,
                                        "CPF ou senha inválidos."
                                )
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (httpRequest, httpResponse) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core apresentou erro interno na autenticação do cliente. HTTP "
                                        + httpResponse.getStatusCode().value()
                        );
                    })
                    .body(CoreLoginResponse.class);

            if (response == null
                    || response.getToken() == null
                    || response.getToken().isBlank()) {
                throw new CoreIntegrationException(
                        "SIGIN Core não retornou token de autenticação do cliente."
                );
            }

            return response;

        } catch (CoreIntegrationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CoreIntegrationException(
                    "Não foi possível autenticar o cliente no SIGIN Core.",
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
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
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

            if (response == null
                    || response.getToken() == null
                    || response.getToken().isBlank()) {
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

    private String extrairMensagemErro(String corpo, String mensagemPadrao) {
        if (corpo == null || corpo.isBlank()) {
            return mensagemPadrao;
        }

        int inicio = corpo.indexOf("\"message\"");

        if (inicio >= 0) {
            int doisPontos = corpo.indexOf(':', inicio);
            int primeiraAspa = corpo.indexOf('"', doisPontos + 1);
            int segundaAspa = corpo.indexOf('"', primeiraAspa + 1);

            if (primeiraAspa >= 0 && segundaAspa > primeiraAspa) {
                String mensagem = corpo.substring(
                        primeiraAspa + 1,
                        segundaAspa
                );

                if (!mensagem.isBlank()) {
                    return mensagem;
                }
            }
        }

        int inicioMensagem = corpo.indexOf("\"mensagem\"");

        if (inicioMensagem >= 0) {
            int doisPontos = corpo.indexOf(':', inicioMensagem);
            int primeiraAspa = corpo.indexOf('"', doisPontos + 1);
            int segundaAspa = corpo.indexOf('"', primeiraAspa + 1);

            if (primeiraAspa >= 0 && segundaAspa > primeiraAspa) {
                String mensagem = corpo.substring(
                        primeiraAspa + 1,
                        segundaAspa
                );

                if (!mensagem.isBlank()) {
                    return mensagem;
                }
            }
        }

        return mensagemPadrao;
    }
}