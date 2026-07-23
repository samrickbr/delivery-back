package br.com.inova.sigin.delivery.pedido.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PedidoPendenciaRequest {

    @NotBlank
    private String motivo;
}