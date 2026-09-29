package com.restaurante.model.dto.response;

public record ItemPedidoResponseDTO(
        Long id,
        Long idPlato,
        String nombrePlato,
        Double precioCongelado,
        Integer cantidad,
        String terminoCoccion,
        String observaciones,
        Double subtotal
) {
}