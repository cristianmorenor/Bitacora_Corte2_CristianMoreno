package com.restaurante.model.dto.response;

public record AuthResponseDTO(
        String token,
        String tipo,
        String username,
        String rol,
        long expiraEnMinutos
) {
}
