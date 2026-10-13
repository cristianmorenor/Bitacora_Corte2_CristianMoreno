package com.restaurante.repository;

import com.restaurante.persistence.document.EventoPedidoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventoPedidoRepository extends MongoRepository<EventoPedidoDocument, String> {

    List<EventoPedidoDocument> findByIdPedidoOrderByFechaAsc(Long idPedido);
}
