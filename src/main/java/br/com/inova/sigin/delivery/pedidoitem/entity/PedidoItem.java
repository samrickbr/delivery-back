package br.com.inova.sigin.delivery.pedidoitem.entity;

import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import br.com.inova.sigin.delivery.produto.entity.Produto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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


    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;


    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;


    private Integer quantidade;


    private BigDecimal valorUnitario;


    private BigDecimal valorTotal;
}