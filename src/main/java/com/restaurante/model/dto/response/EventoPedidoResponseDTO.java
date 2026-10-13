package com.restaurante.model.dto.response;

import java.time.LocalDateTime;

public record EventoPedidoResponseDTO(
        String id,
        Long idPedido,
        Long idMesa,
        String estadoAnterior,
        String estadoNuevo,
        String usuario,
        LocalDateTime fecha
) {
}
