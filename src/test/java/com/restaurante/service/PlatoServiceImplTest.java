package com.restaurante.service;

import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.model.domain.Plato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PlatoServiceImplTest {

    private PlatoServiceImpl platoService;

    @BeforeEach
    void setUp() {
        platoService = new PlatoServiceImpl();
    }

    @Test
    @DisplayName("Crear plato valido - debe asignar id y retornar el plato")
    void crear_platoValido_debeAsignarId() {
        Plato plato = new Plato(null, "Costillas BBQ", 45000.0, "PLATO_FUERTE", true, 20, true);

        Plato creado = platoService.crear(plato);

        assertNotNull(creado.getId());
        assertEquals("Costillas BBQ", creado.getNombre());
        assertTrue(creado.getActivo());
    }

    @Test
    @DisplayName("Crear plato con nombre duplicado - debe lanzar excepcion")
    void crear_nombreDuplicado_debeLanzarExcepcion() {
        Plato plato1 = new Plato(null, "Costillas BBQ", 45000.0, "PLATO_FUERTE", true, 20, null);
        platoService.crear(plato1);

        Plato plato2 = new Plato(null, "Costillas BBQ", 50000.0, "PLATO_FUERTE", true, 20, null);

        assertThrows(PlatoAlreadyExistsException.class, () -> platoService.crear(plato2));
    }

    @Test
    @DisplayName("Obtener plato por id inexistente - debe lanzar excepcion")
    void obtenerPorId_noExiste_debeLanzarExcepcion() {
        assertThrows(PlatoNotFoundException.class, () -> platoService.obtenerPorId(999L));
    }

    @Test
    @DisplayName("Obtener todos sin platos creados - debe retornar lista vacia")
    void obtenerTodos_sinPlatos_debeRetornarListaVacia() {
        List<Plato> resultado = platoService.obtenerTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Eliminar plato existente - debe quitarlo de la lista")
    void eliminar_platoExistente_debeQuitarlo() {
        Plato plato = new Plato(null, "Costillas BBQ", 45000.0, "PLATO_FUERTE", true, 20, null);
        Plato creado = platoService.crear(plato);

        platoService.eliminar(creado.getId());

        assertThrows(PlatoNotFoundException.class, () -> platoService.obtenerPorId(creado.getId()));
    }
}