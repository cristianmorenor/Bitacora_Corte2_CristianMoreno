package com.restaurante.service;

import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;

import java.util.List;

public interface PedidoService {

    Pedido crear(Long idMesa);

    Pedido obtenerPorId(Long id);

    List<Pedido> obtenerTodos();

    Pedido agregarItem(Long idPedido, ItemPedidoRequestDTO itemDto);

    Pedido cambiarEstado(Long idPedido, String nuevoEstado);
}