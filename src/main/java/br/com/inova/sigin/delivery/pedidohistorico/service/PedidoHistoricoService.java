package br.com.inova.sigin.delivery.pedidohistorico.service;

import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedidohistorico.entity.PedidoHistorico;
import br.com.inova.sigin.delivery.pedidohistorico.repository.PedidoHistoricoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PedidoHistoricoService {

    private final PedidoHistoricoRepository repository;

    public void registrar(
            Pedido pedido,
            Long usuarioId,
            String usuarioNome,
            String setor,
            String acao,
            String descricao
    ) {
        repository.save(
                PedidoHistorico.builder()
                        .pedido(pedido)
                        .usuarioId(usuarioId)
                        .usuarioNome(usuarioNome)
                        .setor(setor)
                        .acao(acao)
                        .descricao(descricao)
                        .dataHora(LocalDateTime.now())
                        .build()
        );
    }
}