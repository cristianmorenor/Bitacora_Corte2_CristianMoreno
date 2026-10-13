package com.restaurante.service;

import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.model.domain.Plato;
import com.restaurante.mapper.PlatoPersistenceMapper;
import com.restaurante.persistence.entity.PlatoEntity;
import com.restaurante.repository.PlatoRepository;
import com.restaurante.validator.PlatoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatoServiceImplTest {

    @Mock
    private PlatoRepository platoRepository;

    @Mock
    private PlatoPersistenceMapper entityMapper;

    @Mock
    private PlatoValidator platoValidator;

    @InjectMocks
    private PlatoServiceImpl platoService;

    private Plato plato;
    private PlatoEntity entity;
    private PlatoEntity entityGuardada;
    private Plato platoGuardado;

    @BeforeEach
    void setUp() {
        plato = new Plato(null, "Costillas BBQ", 45000.0, "PLATO_FUERTE", true, 20, true);
        entity = new PlatoEntity(null, "Costillas BBQ", 45000.0, "PLATO_FUERTE", true, 20, true);
        entityGuardada = new PlatoEntity(1L, "Costillas BBQ", 45000.0, "PLATO_FUERTE", true, 20, true);
        platoGuardado = new Plato(1L, "Costillas BBQ", 45000.0, "PLATO_FUERTE", true, 20, true);
    }

    @Test
    @DisplayName("Crear plato valido - debe asignar id y retornar el plato")
    void crear_platoValido_debeAsignarId() {
        when(entityMapper.toEntity(plato)).thenReturn(entity);
        when(platoRepository.save(entity)).thenReturn(entityGuardada);
        when(entityMapper.toDomain(entityGuardada)).thenReturn(platoGuardado);

        Plato creado = platoService.crear(plato);

        assertNotNull(creado.getId());
        assertEquals("Costillas BBQ", creado.getNombre());
        assertTrue(creado.getActivo());
        verify(platoRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Crear plato con nombre duplicado - debe lanzar excepcion")
    void crear_nombreDuplicado_debeLanzarExcepcion() {
        doThrow(new PlatoAlreadyExistsException("Ya existe un plato con nombre: Costillas BBQ"))
                .when(platoValidator).validarNombreUnico("Costillas BBQ");

        assertThrows(PlatoAlreadyExistsException.class, () -> platoService.crear(plato));

        verify(platoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Obtener plato por id inexistente - debe lanzar excepcion")
    void obtenerPorId_noExiste_debeLanzarExcepcion() {
        when(platoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PlatoNotFoundException.class, () -> platoService.obtenerPorId(999L));
    }

    @Test
    @DisplayName("Obtener todos sin platos creados - debe retornar lista vacia")
    void obtenerTodos_sinPlatos_debeRetornarListaVacia() {
        when(platoRepository.findAll()).thenReturn(List.of());

        List<Plato> resultado = platoService.obtenerTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Eliminar plato existente - debe borrarlo del repositorio")
    void eliminar_platoExistente_debeBorrarlo() {
        when(platoRepository.existsById(1L)).thenReturn(true);

        platoService.eliminar(1L);

        verify(platoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar plato inexistente - debe lanzar excepcion")
    void eliminar_platoInexistente_debeLanzarExcepcion() {
        when(platoRepository.existsById(999L)).thenReturn(false);

        assertThrows(PlatoNotFoundException.class, () -> platoService.eliminar(999L));

        verify(platoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Actualizar plato - debe cambiar todos sus datos")
    void actualizar_platoExistente_debeActualizarDatos() {
        Plato cambios = new Plato(null, "Costillas Ahumadas", 48000.0, "PLATO_FUERTE", true, 30, true);
        when(platoRepository.findById(1L)).thenReturn(Optional.of(entityGuardada));
        when(platoRepository.save(entityGuardada)).thenReturn(entityGuardada);
        when(entityMapper.toDomain(entityGuardada)).thenReturn(platoGuardado);

        platoService.actualizar(1L, cambios);

        verify(platoValidator).validarNombreUnicoAlActualizar(1L, "Costillas Ahumadas");
        assertEquals("Costillas Ahumadas", entityGuardada.getNombre());
        assertEquals(48000.0, entityGuardada.getPrecio());
        assertEquals(30, entityGuardada.getMinutosPreparacion());
    }

    @Test
    @DisplayName("Obtener disponibles - debe consultar solo los platos activos")
    void obtenerDisponibles_debeUsarConsultaDeActivos() {
        when(platoRepository.findByActivoTrue()).thenReturn(List.of(entityGuardada));
        when(entityMapper.toDomain(entityGuardada)).thenReturn(platoGuardado);

        List<Plato> resultado = platoService.obtenerDisponibles();

        assertEquals(1, resultado.size());
        verify(platoRepository, never()).findAll();
    }
}
