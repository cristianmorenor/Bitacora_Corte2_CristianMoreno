package com.restaurante.mapper;

import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ItemPedidoMapper.class)
public interface PedidoMapper {

    PedidoResponseDTO toDTO(Pedido pedido);
}