package com.restaurante.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoPedido {

    private String id;
    private Long idPedido;
    private Long idMesa;
    private EstadoPedido estadoAnterior;
    private EstadoPedido estadoNuevo;
    private String usuario;
    private LocalDateTime fecha;
}
