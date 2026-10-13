package com.restaurante.mapper;

import com.restaurante.model.domain.Cuenta;
import com.restaurante.persistence.entity.CuentaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CuentaPersistenceMapper {

    // La mesa la asigna el servicio con la entidad que trae del repositorio
    @Mapping(target = "mesa", ignore = true)
    CuentaEntity toEntity(Cuenta cuenta);

    @Mapping(target = "idMesa", source = "mesa.id")
    Cuenta toDomain(CuentaEntity entity);
}
