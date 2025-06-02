package org.mercadodominio.models.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "IDEMPOTENCY")
public class Idempotency extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "KEY", unique = true, nullable = false)
    private String key;

    @Column(name = "RESPONSE", nullable = false, columnDefinition = "TEXT")
    private String response;
}