package com.restaurante.mapper;

import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.persistence.entity.RegistroVehiculoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegistroVehiculoPersistenceMapper {

    RegistroVehiculoEntity toEntity(RegistroVehiculo registro);

    RegistroVehiculo toDomain(RegistroVehiculoEntity entity);
}
