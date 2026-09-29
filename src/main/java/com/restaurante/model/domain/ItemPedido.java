package com.restaurante.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {

    private Long id;
    private Long idPlato;
    private String nombrePlato;
    private Double precioCongelado;
    private Integer cantidad;
    private TerminoCoccion terminoCoccion;
    private String observaciones;

    public Double subtotal() {
        return precioCongelado * cantidad;
    }
}