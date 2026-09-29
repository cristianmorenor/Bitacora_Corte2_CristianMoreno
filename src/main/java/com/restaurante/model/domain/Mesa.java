package com.restaurante.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mesa {

    private Long id;
    private Integer numero;
    private EstadoMesa estado;

    public Boolean estaDisponible() {
        return estado == EstadoMesa.DISPONIBLE;
    }

    public void ocupar() {
        this.estado = EstadoMesa.OCUPADA;
    }

    public void liberar() {
        this.estado = EstadoMesa.DISPONIBLE;
    }
}