package br.com.inova.sigin.delivery.core.client;

import br.com.inova.sigin.delivery.core.config.CoreClientProperties;
import br.com.inova.sigin.delivery.core.dto.*;
import br.com.inova.sigin.delivery.core.exception.CoreIntegrationException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import br.com.inova.sigin.delivery.cliente.dto.ClienteEnderecoRequest;

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
    public List<FormaPagamentoResponse> listarFormasPagamento() {
        try {
            return restClient.get()
                    .uri("/financeiro/formas-pagamento")
                    .headers(headers -> headers.setBearerAuth(autenticar()))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::is4xxClientError,
                            (request, response) -> {
                                throw new CoreIntegrationException(
                                        "SIGIN Core rejeitou a consulta das formas de pagamento. HTTP "
                                                + response.getStatusCode().value()
                                );
                            }
                    )
                    .onStatus(
                            HttpStatusCode::is5xxServerError,
                            (request, response) -> {
                                throw new CoreIntegrationException(
                                        "SIGIN Core apresentou erro interno ao consultar as formas de pagamento. HTTP "
                                                + response.getStatusCode().value()
                                );
                            }
                    )
                    .body(new ParameterizedTypeReference<List<FormaPagamentoResponse>>() {
                    });

        } catch (CoreIntegrationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CoreIntegrationException(
                    "Não foi possível consultar as formas de pagamento no SIGIN Core.",
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

    public CoreAuthMeResponse buscarAutenticado(String token) {
        try {
            return restClient.get()
                    .uri("/auth/me")
                    .headers(headers -> headers.setBearerAuth(extrairToken(token)))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core rejeitou a identidade autenticada. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core apresentou erro interno ao consultar a identidade. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .body(CoreAuthMeResponse.class);

        } catch (CoreIntegrationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CoreIntegrationException(
                    "Não foi possível consultar a identidade no SIGIN Core.",
                    exception
            );
        }
    }

    public List<PessoaEnderecoResponse> buscarEnderecos(
            Long pessoaId,
            String token
    ) {
        try {
            return restClient.get()
                    .uri("/pessoas/{pessoaId}/enderecos", pessoaId)
                    .headers(headers -> headers.setBearerAuth(extrairToken(token)))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core rejeitou a consulta dos endereços. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core apresentou erro interno ao consultar os endereços. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .body(new ParameterizedTypeReference<List<PessoaEnderecoResponse>>() {
                    });

        } catch (CoreIntegrationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CoreIntegrationException(
                    "Não foi possível consultar os endereços no SIGIN Core.",
                    exception
            );
        }
    }

    private String extrairToken(String authorization) {

        if (authorization == null || authorization.isBlank()) {
            throw new CoreIntegrationException(
                    "Token de autenticação não informado."
            );
        }

        if (!authorization.regionMatches(
                true,
                0,
                "Bearer ",
                0,
                7
        )) {
            throw new CoreIntegrationException(
                    "Formato de autenticação inválido."
            );
        }

        String token = authorization.substring(7).trim();

        if (token.isBlank()) {
            throw new CoreIntegrationException(
                    "Token de autenticação não informado."
            );
        }

        return token;
    }
    public List<PessoaEnderecoResponse> listarMeusEnderecos(
            String token
    ) {
        try {
            return restClient.get()
                    .uri("/api/delivery/clientes/meus-enderecos")
                    .headers(headers -> headers.setBearerAuth(extrairToken(token)))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core rejeitou a consulta dos endereços. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core apresentou erro interno ao consultar os endereços. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .body(new ParameterizedTypeReference<List<PessoaEnderecoResponse>>() {
                    });

        } catch (CoreIntegrationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CoreIntegrationException(
                    "Não foi possível consultar os endereços no SIGIN Core.",
                    exception
            );
        }
    }
    public PessoaEnderecoResponse criarMeuEndereco(
            ClienteEnderecoRequest request,
            String token
    ) {
        try {
            return restClient.post()
                    .uri("/api/delivery/clientes/meus-enderecos")
                    .headers(headers -> headers.setBearerAuth(extrairToken(token)))
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (httpRequest, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core rejeitou a criação do endereço. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (httpRequest, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core apresentou erro interno ao criar o endereço. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .body(PessoaEnderecoResponse.class);

        } catch (CoreIntegrationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CoreIntegrationException(
                    "Não foi possível criar o endereço no SIGIN Core.",
                    exception
            );
        }
    }
    public PessoaEnderecoResponse buscarMeuEndereco(
            Long enderecoId,
            String token
    ) {
        try {
            return restClient.get()
                    .uri(
                            "/api/delivery/clientes/meus-enderecos/{enderecoId}",
                            enderecoId
                    )
                    .headers(headers -> headers.setBearerAuth(extrairToken(token)))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core rejeitou a consulta do endereço. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core apresentou erro interno ao consultar o endereço. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .body(PessoaEnderecoResponse.class);

        } catch (CoreIntegrationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CoreIntegrationException(
                    "Não foi possível consultar o endereço no SIGIN Core.",
                    exception
            );
        }
    }
    public PessoaEnderecoResponse atualizarMeuEndereco(
            Long enderecoId,
            ClienteEnderecoRequest request,
            String token
    ) {
        try {
            return restClient.put()
                    .uri(
                            "/api/delivery/clientes/meus-enderecos/{enderecoId}",
                            enderecoId
                    )
                    .headers(headers -> headers.setBearerAuth(extrairToken(token)))
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (httpRequest, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core rejeitou a atualização do endereço. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (httpRequest, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core apresentou erro interno ao atualizar o endereço. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .body(PessoaEnderecoResponse.class);

        } catch (CoreIntegrationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CoreIntegrationException(
                    "Não foi possível atualizar o endereço no SIGIN Core.",
                    exception
            );
        }
    }
    public PessoaEnderecoResponse definirMeuEnderecoPrincipal(
            Long enderecoId,
            String token
    ) {
        try {
            return restClient.put()
                    .uri(
                            "/api/delivery/clientes/meus-enderecos/{enderecoId}/principal",
                            enderecoId
                    )
                    .headers(headers -> headers.setBearerAuth(extrairToken(token)))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core rejeitou a definição do endereço principal. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core apresentou erro interno ao definir o endereço principal. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .body(PessoaEnderecoResponse.class);

        } catch (CoreIntegrationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CoreIntegrationException(
                    "Não foi possível definir o endereço principal no SIGIN Core.",
                    exception
            );
        }
    }
    public void excluirMeuEndereco(
            Long enderecoId,
            String token
    ) {
        try {
            restClient.delete()
                    .uri(
                            "/api/delivery/clientes/meus-enderecos/{enderecoId}",
                            enderecoId
                    )
                    .headers(headers -> headers.setBearerAuth(extrairToken(token)))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core rejeitou a exclusão do endereço. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new CoreIntegrationException(
                                "SIGIN Core apresentou erro interno ao excluir o endereço. HTTP "
                                        + response.getStatusCode().value()
                        );
                    })
                    .toBodilessEntity();

        } catch (CoreIntegrationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CoreIntegrationException(
                    "Não foi possível excluir o endereço no SIGIN Core.",
                    exception
            );
        }
    }

}