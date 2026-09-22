package com.restaurante.model.dto.response;

public record PlatoResponseDTO(
        Long id,
        String nombre,
        Double precio,
        String categoria,
        Boolean disponible
) {
}