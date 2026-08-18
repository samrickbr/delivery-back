package br.com.inova.sigin.delivery.core.client;

import br.com.inova.sigin.delivery.core.dto.CatalogoItemResponse;
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
}