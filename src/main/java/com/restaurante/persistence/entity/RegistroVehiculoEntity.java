package com.restaurante.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registros_vehiculo")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RegistroVehiculoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String placa;

    @Column(nullable = false)
    private LocalDateTime entrada;

    private LocalDateTime salida;

    private Double cobro;
}