package org.mercadodominio.models.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor 
@Entity
@Table(name = "PRODUTO")
public class Produto extends PanacheEntityBase {
    @Id // Define o campo como chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configura o auto-incremento
    @Column(name = "PRODUTO_ID")
    private Long produtoId;
    @Column(name = "PRODUTO_NOME", nullable = false)
    private String produtoNome;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORIA_ID", nullable = false, foreignKey = @ForeignKey(name = "CATEGORIA_ID"))
    @NotNull(message = "A categoria do produto é obrigatória")
    private Categoria produtoCategoria;
    @Column(name = "PRODUTO_DESCRICAO", nullable = false)
    private String produtoDescricao;
    @Column(name = "PRODUTO_PRECO", nullable = false)
    private Double produtoPreco;
}