package com.restaurante.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroVehiculo {

    private Long id;
    private String placa;
    private LocalDateTime entrada;
    private LocalDateTime salida;
    private Double cobro;

    public Double calcularCobro() {
        if (salida == null) {
            return 0.0;
        }
        long minutos = Duration.between(entrada, salida).toMinutes();
        return minutos * 100.0;
    }

    public void registrarSalida() {
        this.salida = LocalDateTime.now();
        this.cobro = calcularCobro();
    }

    public Boolean estaActivo() {
        return salida == null;
    }
}