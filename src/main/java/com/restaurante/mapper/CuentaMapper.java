package com.restaurante.mapper;

import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.dto.response.CuentaResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CuentaMapper {

    CuentaResponseDTO toDTO(Cuenta cuenta);
}