package com.restaurante.mapper;

import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlatoEntityMapper {

    PlatoResponseDTO toDTO(Plato plato);
}