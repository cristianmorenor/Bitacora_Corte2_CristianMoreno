package com.restaurante.model.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ReservaRequestDTO(
        @NotNull(message = "El id de la mesa es obligatorio")
        Long idMesa,

        @NotBlank(message = "El nombre del cliente es obligatorio")
        @Size(max = 100, message = "El nombre del cliente no puede superar 100 caracteres")
        String cliente,

        @NotNull(message = "La fecha y hora de la reserva es obligatoria")
        @Future(message = "La reserva debe ser en una fecha futura")
        LocalDateTime fechaHora,

        @NotNull(message = "El numero de comensales es obligatorio")
        @Positive(message = "Los comensales deben ser mayor a cero")
        Integer comensales
) {
}
