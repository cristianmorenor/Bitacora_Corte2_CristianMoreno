package com.restaurante.mapper;

import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.dto.response.ItemPedidoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemPedidoMapper {

    @Mapping(target = "subtotal", expression = "java(item.subtotal())")
    ItemPedidoResponseDTO toDTO(ItemPedido item);
}