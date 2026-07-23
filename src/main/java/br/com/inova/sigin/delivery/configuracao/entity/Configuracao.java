package br.com.inova.sigin.delivery.configuracao.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "configuracao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Configuracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String empresa;

    private String telefone;

    private String whatsapp;

    private BigDecimal taxaEntrega;

    private Boolean ativo;
}