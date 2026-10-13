package com.restaurante.model.dto.response;

public record UsuarioResponseDTO(
        Long id,
        String username,
        String rol,
        Boolean activo
) {
}
