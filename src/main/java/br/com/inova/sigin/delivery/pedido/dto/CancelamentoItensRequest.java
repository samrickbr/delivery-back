package br.com.inova.sigin.delivery.pedido.dto;

import lombok.Data;

import java.util.List;

@Data
public class CancelamentoItensRequest {

    private List<Long> itens;

    private String justificativa;
}