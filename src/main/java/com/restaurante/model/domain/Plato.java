package com.restaurante.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plato {

    private Long id;
    private String nombre;
    private Double precio;
    private String categoria;
    private Boolean esCorte;
    private Integer minutosPreparacion;
    private Boolean activo;

    public Boolean esValido() {
        return nombre != null && !nombre.isBlank()
                && precio != null && precio > 0;
    }

    public Boolean estaDisponible() {
        return Boolean.TRUE.equals(activo);
    }
}