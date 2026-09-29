package com.restaurante.model.dto.request;

import com.restaurante.model.domain.TerminoCoccion;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemPedidoRequestDTO(

        @NotNull(message = "El id del plato es obligatorio")
        Long idPlato,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        Integer cantidad,

        TerminoCoccion terminoCoccion,

        String observaciones

) {
}