package com.restaurante.mapper;

import com.restaurante.model.domain.EventoPedido;
import com.restaurante.model.dto.response.EventoPedidoResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventoPedidoMapper {

    EventoPedidoResponseDTO toDTO(EventoPedido evento);
}
