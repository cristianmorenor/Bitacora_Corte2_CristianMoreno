package com.restaurante.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "platos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PlatoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(name = "es_corte", nullable = false)
    private Boolean esCorte;

    @Column(name = "minutos_preparacion")
    private Integer minutosPreparacion;

    @Column(nullable = false)
    private Boolean activo;
}