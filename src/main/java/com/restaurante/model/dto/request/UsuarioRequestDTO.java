package com.restaurante.model.dto.request;

import com.restaurante.model.domain.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(
        @NotBlank(message = "El usuario es obligatorio")
        @Size(min = 4, max = 50, message = "El usuario debe tener entre 4 y 50 caracteres")
        String username,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 8, max = 72, message = "La contrasena debe tener entre 8 y 72 caracteres")
        String password,

        @NotNull(message = "El rol es obligatorio")
        Rol rol
) {
}
