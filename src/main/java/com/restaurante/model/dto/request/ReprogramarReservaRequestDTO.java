package com.restaurante.model.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReprogramarReservaRequestDTO(
        @NotNull(message = "La nueva fecha y hora es obligatoria")
        @Future(message = "La reserva debe ser en una fecha futura")
        LocalDateTime fechaHora
) {
}
