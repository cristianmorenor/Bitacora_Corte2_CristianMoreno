package com.restaurante.service;

import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MesaServiceImplTest {

    private MesaServiceImpl mesaService;

    @BeforeEach
    void setUp() {
        mesaService = new MesaServiceImpl();
    }

    @Test
    @DisplayName("Crear mesa - debe asignar id y estado DISPONIBLE")
    void crear_mesaValida_debeAsignarIdYEstadoDisponible() {
        Mesa mesa = new Mesa(null, 1, EstadoMesa.DISPONIBLE);

        Mesa creada = mesaService.crear(mesa);

        assertNotNull(creada.getId());
        assertEquals(EstadoMesa.DISPONIBLE, creada.getEstado());
    }

    @Test
    @DisplayName("Obtener mesa por id inexistente - debe lanzar excepcion")
    void obtenerPorId_noExiste_debeLanzarExcepcion() {
        assertThrows(MesaNotFoundException.class, () -> mesaService.obtenerPorId(999L));
    }

    @Test
    @DisplayName("Obtener todas sin mesas creadas - debe retornar lista vacia")
    void obtenerTodas_sinMesas_debeRetornarListaVacia() {
        List<Mesa> resultado = mesaService.obtenerTodas();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Ocupar mesa - debe cambiar estado a OCUPADA")
    void ocupar_mesaExistente_debeCambiarEstado() {
        Mesa mesa = new Mesa(null, 1, EstadoMesa.DISPONIBLE);
        Mesa creada = mesaService.crear(mesa);

        mesaService.ocupar(creada.getId());

        Mesa actualizada = mesaService.obtenerPorId(creada.getId());
        assertEquals(EstadoMesa.OCUPADA, actualizada.getEstado());
    }

    @Test
    @DisplayName("Liberar mesa - debe cambiar estado a DISPONIBLE")
    void liberar_mesaOcupada_debeCambiarEstado() {
        Mesa mesa = new Mesa(null, 1, EstadoMesa.OCUPADA);
        Mesa creada = mesaService.crear(mesa);

        mesaService.liberar(creada.getId());

        Mesa actualizada = mesaService.obtenerPorId(creada.getId());
        assertEquals(EstadoMesa.DISPONIBLE, actualizada.getEstado());
    }
}