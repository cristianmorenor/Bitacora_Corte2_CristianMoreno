package com.restaurante.model.dto.response;

import java.time.LocalDateTime;

public record CuentaResponseDTO(
        Long id,
        Long idMesa,
        Double total,
        String estado,
        LocalDateTime apertura
) {
}