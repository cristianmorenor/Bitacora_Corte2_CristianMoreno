package com.restaurante.repository;

import com.restaurante.persistence.entity.PlatoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PlatoRepositoryTest {

    @Autowired
    private PlatoRepository platoRepository;

    private PlatoEntity picanha;

    @BeforeEach
    void setUp() {
        picanha = platoRepository.save(new PlatoEntity(null, "Picanha", 52000.0, "CORTES", true, 25, true));
        platoRepository.save(new PlatoEntity(null, "Chorizo", 9000.0, "ENTRADAS", false, 10, false));
    }

    @Test
    @DisplayName("save - la base de datos debe asignar el id (IDENTITY)")
    void save_debeAsignarId() {
        assertNotNull(picanha.getId());
    }

    @Test
    @DisplayName("existsByNombreIgnoreCase - no debe importar mayusculas")
    void existsByNombreIgnoreCase_debeIgnorarMayusculas() {
        assertTrue(platoRepository.existsByNombreIgnoreCase("PICANHA"));
        assertFalse(platoRepository.existsByNombreIgnoreCase("Bife"));
    }

    @Test
    @DisplayName("existsByNombreIgnoreCaseAndIdNot - debe excluir al propio plato")
    void existsByNombreIgnoreCaseAndIdNot_debeExcluirPropioPlato() {
        assertFalse(platoRepository.existsByNombreIgnoreCaseAndIdNot("Picanha", picanha.getId()));
        assertTrue(platoRepository.existsByNombreIgnoreCaseAndIdNot("Picanha", picanha.getId() + 100));
    }

    @Test
    @DisplayName("findByActivoTrue - solo debe traer platos activos")
    void findByActivoTrue_debeFiltrarActivos() {
        List<PlatoEntity> activos = platoRepository.findByActivoTrue();

        assertEquals(1, activos.size());
        assertEquals("Picanha", activos.get(0).getNombre());
    }
}
