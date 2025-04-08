package org.mercadodominio.models.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*; 

@Getter
@Setter 
@NoArgsConstructor
@AllArgsConstructor 
@Entity
@Table(name = "CATEGORIA")
public class Categoria extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORIA_ID")
    private Long categoriaId;
    @Column(name = "CATEGORIA_NOME", nullable = false)
    private String categoriaNome;
}
