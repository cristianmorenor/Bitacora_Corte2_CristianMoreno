package com.restaurante.model.dto.request;

import jakarta.validation.constraints.NotNull;

public record PedidoRequestDTO(

        @NotNull(message = "El id de la mesa es obligatorio")
        Long idMesa

) {
}
