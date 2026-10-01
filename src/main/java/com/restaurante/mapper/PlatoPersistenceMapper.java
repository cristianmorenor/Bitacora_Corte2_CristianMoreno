package com.restaurante.mapper;

import com.restaurante.model.domain.Plato;
import com.restaurante.persistence.entity.PlatoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlatoPersistenceMapper {

    PlatoEntity toEntity(Plato plato);

    Plato toDomain(PlatoEntity entity);
}