package com.restaurante.mapper;

import com.restaurante.model.domain.Reserva;
import com.restaurante.model.dto.request.ReservaRequestDTO;
import com.restaurante.model.dto.response.ReservaResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    @Mapping(target = "id", ignore = true)
    Reserva toDomain(ReservaRequestDTO dto);

    @Mapping(target = "vigente", expression = "java(reserva.estaVigente())")
    ReservaResponseDTO toDTO(Reserva reserva);
}
