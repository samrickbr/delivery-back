package br.com.inova.sigin.delivery.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoreLoginRequest {

    private String login;

    private String senha;
}