package com.restaurante.mapper;

import com.restaurante.model.domain.Reserva;
import com.restaurante.persistence.entity.ReservaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservaPersistenceMapper {

    // La mesa la asigna el servicio con la entidad que trae del repositorio
    @Mapping(target = "mesa", ignore = true)
    ReservaEntity toEntity(Reserva reserva);

    @Mapping(target = "idMesa", source = "mesa.id")
    Reserva toDomain(ReservaEntity entity);
}
