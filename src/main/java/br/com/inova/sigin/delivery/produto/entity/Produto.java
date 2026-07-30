package br.com.inova.sigin.delivery.produto.entity;

import br.com.inova.sigin.delivery.categoria.entity.Categoria;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;


    private String nome;

    private String descricao;

    private BigDecimal preco;

    private String imagem;

    private Boolean disponivel;

    private Boolean ativo;

}