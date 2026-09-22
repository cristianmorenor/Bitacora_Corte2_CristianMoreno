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
    private Integer capacidad;
    private EstadoMesa estado;
    private Boolean cuentaAbierta;

    public Boolean estaDisponible() {
        return estado == EstadoMesa.DISPONIBLE;
    }

    public void abrirCuenta() {
        this.cuentaAbierta = true;
        this.estado = EstadoMesa.OCUPADA;
    }

    public void cerrarCuenta() {
        this.cuentaAbierta = false;
        this.estado = EstadoMesa.DISPONIBLE;
    }
}