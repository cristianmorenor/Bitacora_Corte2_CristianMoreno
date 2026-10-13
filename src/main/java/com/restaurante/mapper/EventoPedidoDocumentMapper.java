package com.restaurante.mapper;

import com.restaurante.model.domain.EventoPedido;
import com.restaurante.persistence.document.EventoPedidoDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventoPedidoDocumentMapper {

    EventoPedidoDocument toDocument(EventoPedido evento);

    EventoPedido toDomain(EventoPedidoDocument document);
}
