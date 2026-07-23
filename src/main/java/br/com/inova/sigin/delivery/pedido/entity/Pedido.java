package br.com.inova.sigin.delivery.pedido.entity;

import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clienteNome;

    private String clienteWhatsapp;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @Column(length = 500)
    private String observacaoOperacao;

    private BigDecimal valorProdutos;

    private BigDecimal taxaEntrega;

    private BigDecimal valorTotal;

    private String formaPagamento;

    private String observacao;

    private LocalDateTime dataCriacao;
}