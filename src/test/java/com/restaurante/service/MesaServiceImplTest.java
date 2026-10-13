package com.restaurante.service;

import com.restaurante.exception.MesaAlreadyExistsException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.mapper.MesaPersistenceMapper;
import com.restaurante.mapper.MesaPersistenceMapperImpl;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.repository.MesaRepository;
import com.restaurante.validator.MesaValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MesaServiceImplTest {

    @Mock
    private MesaRepository mesaRepository;

    @Mock
    private MesaValidator mesaValidator;

    @Spy
    private MesaPersistenceMapper entityMapper = new MesaPersistenceMapperImpl();

    @InjectMocks
    private MesaServiceImpl mesaService;

    @Test
    @DisplayName("Crear mesa - debe asignar id y estado DISPONIBLE")
    void crear_mesaValida_debeAsignarIdYEstadoDisponible() {
        when(mesaRepository.save(any(MesaEntity.class)))
                .thenReturn(new MesaEntity(1L, 1, EstadoMesa.DISPONIBLE));

        Mesa creada = mesaService.crear(new Mesa(null, 1, EstadoMesa.DISPONIBLE));

        assertNotNull(creada.getId());
        assertEquals(EstadoMesa.DISPONIBLE, creada.getEstado());
        verify(mesaValidator).validarNumeroUnico(1);
    }

    @Test
    @DisplayName("Crear mesa con numero repetido - no debe guardarla")
    void crear_numeroRepetido_debeLanzarExcepcion() {
        doThrow(new MesaAlreadyExistsException("Ya existe una mesa con numero: 1"))
                .when(mesaValidator).validarNumeroUnico(1);

        assertThrows(MesaAlreadyExistsException.class,
                () -> mesaService.crear(new Mesa(null, 1, EstadoMesa.DISPONIBLE)));
        verify(mesaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Obtener mesa por id inexistente - debe lanzar excepcion")
    void obtenerPorId_noExiste_debeLanzarExcepcion() {
        when(mesaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(MesaNotFoundException.class, () -> mesaService.obtenerPorId(999L));
    }

    @Test
    @DisplayName("Obtener todas sin mesas creadas - debe retornar lista vacia")
    void obtenerTodas_sinMesas_debeRetornarListaVacia() {
        when(mesaRepository.findAll()).thenReturn(List.of());

        List<Mesa> resultado = mesaService.obtenerTodas();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Ocupar mesa - debe cambiar estado a OCUPADA y guardarla")
    void ocupar_mesaExistente_debeCambiarEstado() {
        MesaEntity entity = new MesaEntity(1L, 1, EstadoMesa.DISPONIBLE);
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(entity));

        mesaService.ocupar(1L);

        assertEquals(EstadoMesa.OCUPADA, entity.getEstado());
        verify(mesaRepository).save(entity);
    }

    @Test
    @DisplayName("Liberar mesa - debe cambiar estado a DISPONIBLE y guardarla")
    void liberar_mesaOcupada_debeCambiarEstado() {
        MesaEntity entity = new MesaEntity(1L, 1, EstadoMesa.OCUPADA);
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(entity));

        mesaService.liberar(1L);

        assertEquals(EstadoMesa.DISPONIBLE, entity.getEstado());
        verify(mesaRepository).save(entity);
    }
}
