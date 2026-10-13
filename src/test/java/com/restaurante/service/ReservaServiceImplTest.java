package com.restaurante.service;

import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.exception.ReservaNotFoundException;
import com.restaurante.mapper.ReservaPersistenceMapper;
import com.restaurante.mapper.ReservaPersistenceMapperImpl;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Reserva;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.persistence.entity.ReservaEntity;
import com.restaurante.repository.MesaRepository;
import com.restaurante.repository.ReservaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private MesaRepository mesaRepository;

    @Spy
    private ReservaPersistenceMapper entityMapper = new ReservaPersistenceMapperImpl();

    @InjectMocks
    private ReservaServiceImpl reservaService;

    private final MesaEntity mesa = new MesaEntity(1L, 1, EstadoMesa.DISPONIBLE);
    private final LocalDateTime manana = LocalDateTime.now().plusDays(1);

    @Test
    @DisplayName("Crear reserva - debe guardarla asociada a la mesa")
    void crear_reservaValida_debeGuardarla() {
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));
        when(reservaRepository.save(any(ReservaEntity.class))).thenAnswer(inv -> {
            ReservaEntity e = inv.getArgument(0);
            e.setId(3L);
            return e;
        });

        Reserva creada = reservaService.crear(new Reserva(null, 1L, "Ana", manana, 4));

        assertEquals(3L, creada.getId());
        assertEquals(1L, creada.getIdMesa());
        assertTrue(creada.estaVigente());
    }

    @Test
    @DisplayName("Crear reserva en mesa inexistente - debe lanzar excepcion")
    void crear_mesaNoExiste_debeLanzarExcepcion() {
        when(mesaRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(MesaNotFoundException.class,
                () -> reservaService.crear(new Reserva(null, 9L, "Ana", manana, 4)));
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cancelar reserva - debe dejarla sin fecha y no vigente")
    void cancelar_reservaExistente_debeDejarlaNoVigente() {
        ReservaEntity entity = new ReservaEntity(3L, mesa, "Ana", manana, 4);
        when(reservaRepository.findById(3L)).thenReturn(Optional.of(entity));

        Reserva cancelada = reservaService.cancelar(3L);

        assertFalse(cancelada.estaVigente());
        assertNull(entity.getFechaHora());
        verify(reservaRepository).save(entity);
    }

    @Test
    @DisplayName("Reprogramar reserva - debe actualizar la fecha")
    void reprogramar_reservaExistente_debeCambiarFecha() {
        ReservaEntity entity = new ReservaEntity(3L, mesa, "Ana", manana, 4);
        LocalDateTime nuevaFecha = manana.plusDays(2);
        when(reservaRepository.findById(3L)).thenReturn(Optional.of(entity));

        Reserva reprogramada = reservaService.reprogramar(3L, nuevaFecha);

        assertEquals(nuevaFecha, reprogramada.getFechaHora());
        assertEquals(nuevaFecha, entity.getFechaHora());
    }

    @Test
    @DisplayName("Obtener reservas por mesa - debe usar la consulta por mesa")
    void obtenerPorMesa_debeConsultarPorMesa() {
        when(reservaRepository.findByMesaId(1L)).thenReturn(List.of(new ReservaEntity(3L, mesa, "Ana", manana, 4)));

        List<Reserva> reservas = reservaService.obtenerPorMesa(1L);

        assertEquals(1, reservas.size());
        assertEquals("Ana", reservas.get(0).getCliente());
    }

    @Test
    @DisplayName("Eliminar reserva inexistente - debe lanzar excepcion")
    void eliminar_noExiste_debeLanzarExcepcion() {
        when(reservaRepository.existsById(9L)).thenReturn(false);

        assertThrows(ReservaNotFoundException.class, () -> reservaService.eliminar(9L));
        verify(reservaRepository, never()).deleteById(any());
    }
}
