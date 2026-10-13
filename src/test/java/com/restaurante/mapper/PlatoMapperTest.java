package com.restaurante.mapper;

import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.persistence.entity.PlatoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlatoMapperTest {

    private final PlatoMapper platoMapper = new PlatoMapperImpl();
    private final PlatoPersistenceMapper persistenceMapper = new PlatoPersistenceMapperImpl();

    @Test
    @DisplayName("RequestDTO a dominio - debe quedar activo y sin id")
    void toDomain_desdeRequest_debeQuedarActivoSinId() {
        PlatoRequestDTO dto = new PlatoRequestDTO("Picanha", 52000.0, "CORTES", true, 25);

        Plato plato = platoMapper.toDomain(dto);

        assertNull(plato.getId());
        assertTrue(plato.getActivo());
        assertEquals("Picanha", plato.getNombre());
        assertTrue(plato.getEsCorte());
    }

    @Test
    @DisplayName("Dominio a entidad y de vuelta - no debe perder datos")
    void persistencia_idaYVuelta_debeConservarDatos() {
        Plato original = new Plato(7L, "Picanha", 52000.0, "CORTES", true, 25, true);

        PlatoEntity entity = persistenceMapper.toEntity(original);
        Plato deVuelta = persistenceMapper.toDomain(entity);

        assertEquals(original, deVuelta);
    }
}
