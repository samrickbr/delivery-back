package br.com.inova.sigin.delivery.pedidohistorico.entity;

import br.com.inova.sigin.delivery.pedido.entity.Pedido;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pedido_historico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    private Long usuarioId;

    private String usuarioNome;

    private String setor;

    private String acao;

    @Column(length = 1000)
    private String descricao;

    private LocalDateTime dataHora;
}