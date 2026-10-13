package com.restaurante.service;

import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.EventoPedido;
import com.restaurante.model.domain.Pedido;

import java.util.List;

public interface AuditoriaPedidoService {

    void registrarCambioEstado(Pedido pedido, EstadoPedido estadoAnterior);

    List<EventoPedido> obtenerHistorial(Long idPedido);
}
