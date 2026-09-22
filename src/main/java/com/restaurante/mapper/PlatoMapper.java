package com.restaurante.mapper;

import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlatoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "disponible", constant = "true")
    Plato toDomain(PlatoRequestDTO dto);
}