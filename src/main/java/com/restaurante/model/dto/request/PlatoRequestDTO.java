package com.restaurante.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PlatoRequestDTO(

        @NotBlank(message = "El nombre del plato es obligatorio")
        @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
        String nombre,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
        Double precio,

        @NotBlank(message = "La categoria es obligatoria")
        String categoria,

        @NotNull(message = "Debe indicar si es un corte de parrilla")
        Boolean esCorte,

        @NotNull(message = "Los minutos de preparacion son obligatorios")
        Integer minutosPreparacion

) {
}