package com.restaurante.mapper;

import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ItemPedidoMapper.class)
public interface PedidoMapper {

    @Mapping(target = "total", expression = "java(pedido.calcularTotal())")
    PedidoResponseDTO toDTO(Pedido pedido);
}
