package com.restaurante.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    private Long id;
    private Long idMesa;
    private List<ItemPedido> items;
    private EstadoPedido estado;
    private LocalDateTime timestamp;

    public Boolean puedeModificarse() {
        return estado == EstadoPedido.RECIBIDO;
    }

    public void agregarItem(ItemPedido item) {
        this.items.add(item);
    }

    public void cambiarEstado(EstadoPedido nuevoEstado) {
        this.estado = nuevoEstado;
    }
}