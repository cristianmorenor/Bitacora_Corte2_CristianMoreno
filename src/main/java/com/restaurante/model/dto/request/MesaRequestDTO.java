package com.restaurante.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MesaRequestDTO(

        @NotNull(message = "El numero de mesa es obligatorio")
        @Positive(message = "El numero de mesa debe ser positivo")
        Integer numero

) {
}