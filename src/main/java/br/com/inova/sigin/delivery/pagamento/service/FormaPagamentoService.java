package br.com.inova.sigin.delivery.pagamento.service;

import br.com.inova.sigin.delivery.core.client.CoreClient;
import br.com.inova.sigin.delivery.core.dto.FormaPagamentoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormaPagamentoService {

    private final CoreClient coreClient;

    public List<FormaPagamentoResponse> listar() {
        return coreClient.listarFormasPagamento();
    }
}