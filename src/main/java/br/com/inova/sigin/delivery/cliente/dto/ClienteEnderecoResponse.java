package br.com.inova.sigin.delivery.cliente.dto;

import br.com.inova.sigin.delivery.core.dto.PessoaEnderecoResponse;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ClienteEnderecoResponse {

    private Long id;

    private String logradouro;

    private String numero;

    private String complemento;

    private String bairro;

    private String cidade;

    private String estado;

    private String cep;

    private Boolean principal;

    public static ClienteEnderecoResponse from(
            PessoaEnderecoResponse endereco
    ) {
        return ClienteEnderecoResponse.builder()
                .id(endereco.getId())
                .logradouro(endereco.getLogradouro())
                .numero(endereco.getNumero())
                .complemento(endereco.getComplemento())
                .bairro(endereco.getBairro())
                .cidade(endereco.getCidade())
                .estado(endereco.getUf())
                .cep(endereco.getCep())
                .principal(endereco.getPrincipal())
                .build();
    }
}