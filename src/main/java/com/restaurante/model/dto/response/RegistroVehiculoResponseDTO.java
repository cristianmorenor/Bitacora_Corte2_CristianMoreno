package com.restaurante.model.dto.response;

import java.time.LocalDateTime;

public record RegistroVehiculoResponseDTO(
        Long id,
        String placa,
        LocalDateTime entrada,
        LocalDateTime salida,
        Double cobro,
        Boolean activo
) {
}
