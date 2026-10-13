package com.restaurante.mapper;

import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.persistence.entity.ItemPedidoEntity;
import com.restaurante.persistence.entity.PedidoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PedidoPersistenceMapper {

    @Mapping(target = "idMesa", source = "mesa.id")
    Pedido toDomain(PedidoEntity entity);

    @Mapping(target = "idPlato", source = "plato.id")
    ItemPedido toDomain(ItemPedidoEntity entity);
}
