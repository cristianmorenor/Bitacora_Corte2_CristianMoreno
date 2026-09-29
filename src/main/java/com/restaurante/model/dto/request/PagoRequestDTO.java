package com.restaurante.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record PagoRequestDTO(

        @NotNull(message = "El monto del pago es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
        Double monto

) {
}