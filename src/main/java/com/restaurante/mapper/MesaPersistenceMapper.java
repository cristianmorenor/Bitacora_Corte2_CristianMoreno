package com.restaurante.mapper;

import com.restaurante.model.domain.Mesa;
import com.restaurante.persistence.entity.MesaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MesaPersistenceMapper {

    MesaEntity toEntity(Mesa mesa);

    Mesa toDomain(MesaEntity entity);
}
