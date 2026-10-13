package com.restaurante.model.dto.response;

import java.time.LocalDateTime;

public record ReservaResponseDTO(
        Long id,
        Long idMesa,
        String cliente,
        LocalDateTime fechaHora,
        Integer comensales,
        Boolean vigente
) {
}
