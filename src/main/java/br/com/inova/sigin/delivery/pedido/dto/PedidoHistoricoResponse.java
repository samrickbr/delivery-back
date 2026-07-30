package br.com.inova.sigin.delivery.pedido.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PedidoHistoricoResponse {

    private LocalDateTime dataHora;

    private String usuario;

    private String setor;

    private String acao;

    private String descricao;

}