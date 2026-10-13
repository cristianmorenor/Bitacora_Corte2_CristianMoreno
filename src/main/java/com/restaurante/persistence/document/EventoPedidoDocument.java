package com.restaurante.persistence.document;

import com.restaurante.model.domain.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "eventos_pedido")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class EventoPedidoDocument {

    // ObjectId generado por Mongo: miles de inserts sin fragmentar el indice
    @Id
    private String id;

    @Indexed
    private Long idPedido;

    private Long idMesa;

    private EstadoPedido estadoAnterior;

    private EstadoPedido estadoNuevo;

    private String usuario;

    private LocalDateTime fecha;
}
