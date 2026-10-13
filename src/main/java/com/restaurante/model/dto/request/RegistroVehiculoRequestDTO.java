package com.restaurante.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegistroVehiculoRequestDTO(
        @NotBlank(message = "La placa es obligatoria")
        @Pattern(regexp = "^[A-Za-z]{3}-?[0-9]{2}[0-9A-Za-z]$", message = "La placa debe tener el formato ABC123 o ABC12D")
        String placa
) {
}
