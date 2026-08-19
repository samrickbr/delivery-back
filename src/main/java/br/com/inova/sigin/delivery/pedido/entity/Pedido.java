package br.com.inova.sigin.delivery.pedido.entity;

import br.com.inova.sigin.delivery.pedido.enums.StatusPedido;
import br.com.inova.sigin.delivery.pedidohistorico.entity.PedidoHistorico;
import br.com.inova.sigin.delivery.pedidoitem.entity.PedidoItem;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private Long clienteId;

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

    @Column(nullable = false)
    private LocalDateTime statusAlteradoEm;

    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("dataHora DESC")
    @Builder.Default
    private List<PedidoHistorico> historico = new ArrayList<>();

    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<PedidoItem> itens = new ArrayList<>();

    private LocalDateTime conferenciaEm;

    private Long conferenciaPorUsuarioId;
}