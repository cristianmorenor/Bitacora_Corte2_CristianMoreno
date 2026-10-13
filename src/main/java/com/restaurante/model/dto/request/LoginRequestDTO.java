package com.restaurante.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "El usuario es obligatorio")
        String username,

        @NotBlank(message = "La contrasena es obligatoria")
        String password
) {
}
