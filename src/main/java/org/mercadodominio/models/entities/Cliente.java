package org.mercadodominio.models.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor // Gera um construtor com todos os argumentos
@NoArgsConstructor // Gera um construtor sem argumentos
@Entity
@Table(name = "CLIENTE")
public class Cliente extends PanacheEntityBase {

    @Id // Define o campo como chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configura o auto-incremento
    @Column(name = "CLIENTE_ID")
    private Long clienteId;
    @Column(name = "CLIENTE_NOME", nullable = false)
    private String clienteNome;
    @Column(name = "CLIENTE_EMAIL", nullable = false, unique = true)
    private String clienteEmail;
    @Column(name = "CLIENTE_IDADE", nullable = false)
    private int clienteIdade;
}
