package com.restaurante.mapper;

import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.model.dto.response.RegistroVehiculoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegistroVehiculoMapper {

    @Mapping(target = "activo", expression = "java(registro.estaActivo())")
    RegistroVehiculoResponseDTO toDTO(RegistroVehiculo registro);
}
