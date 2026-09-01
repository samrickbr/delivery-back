package br.com.inova.sigin.delivery.pedidoitem.entity;

import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.pedidoitem.enums.StatusOperacao;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Referência técnica ao item oficial do pedido no SIGIN Core.
     *
     * Permite sincronizar o item comercial sem perder
     * o estado operacional mantido pelo Delivery.
     */
    @Column(name = "core_item_id", nullable = false)
    private Long coreItemId;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    /**
     * Referência técnica ao produto oficial do SIGIN Core.
     *
     * O Delivery não mantém relacionamento com Produto local.
     */
    @Column(name = "core_produto_id", nullable = false)
    private Long coreProdutoId;

    /**
     * Snapshot do nome do produto retornado pelo Core.
     */
    private String produtoNome;

    private Integer quantidade;

    private BigDecimal valorUnitario;

    private BigDecimal valorTotal;

    /**
     * Snapshot do setor retornado pelo SIGIN Core.
     */
    private String setor;

    // ==========================================
    // CHECKLIST DE SEPARAÇÃO
    // ==========================================

    @Builder.Default
    private Boolean separado = false;

    @Enumerated(EnumType.STRING)
    private StatusOperacao statusOperacao;

    // ==========================================
    // HISTORICO CANCELAMENTO ITEM
    // ==========================================

    private String motivoCancelamento;

    private LocalDateTime canceladoEm;

    private String canceladoPor;
}