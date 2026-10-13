package com.restaurante.service;

import com.restaurante.exception.RegistroVehiculoNotFoundException;
import com.restaurante.exception.SalidaYaRegistradaException;
import com.restaurante.exception.VehiculoYaRegistradoException;
import com.restaurante.mapper.RegistroVehiculoPersistenceMapper;
import com.restaurante.mapper.RegistroVehiculoPersistenceMapperImpl;
import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.persistence.entity.RegistroVehiculoEntity;
import com.restaurante.repository.RegistroVehiculoRepository;
import com.restaurante.validator.RegistroVehiculoValidator;
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
class RegistroVehiculoServiceImplTest {

    @Mock
    private RegistroVehiculoRepository registroRepository;

    @Mock
    private RegistroVehiculoValidator registroValidator;

    @Spy
    private RegistroVehiculoPersistenceMapper entityMapper = new RegistroVehiculoPersistenceMapperImpl();

    @InjectMocks
    private RegistroVehiculoServiceImpl registroService;

    @Test
    @DisplayName("Registrar entrada - debe normalizar la placa y guardar la hora de entrada")
    void registrarEntrada_placaValida_debeGuardarRegistro() {
        when(registroRepository.save(any(RegistroVehiculoEntity.class))).thenAnswer(inv -> {
            RegistroVehiculoEntity e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });

        RegistroVehiculo registro = registroService.registrarEntrada("abc-123");

        assertEquals("ABC123", registro.getPlaca());
        assertNotNull(registro.getEntrada());
        assertTrue(registro.estaActivo());
        verify(registroValidator).validarSinRegistroActivo("ABC123");
    }

    @Test
    @DisplayName("Registrar entrada de un vehiculo que ya esta adentro - debe lanzar excepcion")
    void registrarEntrada_vehiculoAdentro_debeLanzarExcepcion() {
        doThrow(new VehiculoYaRegistradoException("ya esta")).when(registroValidator).validarSinRegistroActivo("ABC123");

        assertThrows(VehiculoYaRegistradoException.class, () -> registroService.registrarEntrada("ABC123"));
        verify(registroRepository, never()).save(any());
    }

    @Test
    @DisplayName("Registrar salida - debe calcular el cobro por minutos")
    void registrarSalida_registroActivo_debeCalcularCobro() {
        RegistroVehiculoEntity entity = new RegistroVehiculoEntity(1L, "ABC123", LocalDateTime.now().minusMinutes(30), null, null);
        when(registroRepository.findById(1L)).thenReturn(Optional.of(entity));

        RegistroVehiculo registro = registroService.registrarSalida(1L);

        assertFalse(registro.estaActivo());
        assertTrue(registro.getCobro() >= 3000.0);
        assertEquals(registro.getCobro(), entity.getCobro());
    }

    @Test
    @DisplayName("Registrar salida dos veces - debe lanzar excepcion")
    void registrarSalida_yaRegistrada_debeLanzarExcepcion() {
        LocalDateTime entrada = LocalDateTime.now().minusHours(1);
        RegistroVehiculoEntity entity = new RegistroVehiculoEntity(1L, "ABC123", entrada, entrada.plusMinutes(10), 1000.0);
        when(registroRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertThrows(SalidaYaRegistradaException.class, () -> registroService.registrarSalida(1L));
    }

    @Test
    @DisplayName("Obtener activos - debe consultar solo los vehiculos sin salida")
    void obtenerActivos_debeUsarConsultaDeActivos() {
        when(registroRepository.findBySalidaIsNull()).thenReturn(List.of());

        assertTrue(registroService.obtenerActivos().isEmpty());
        verify(registroRepository, never()).findAll();
    }

    @Test
    @DisplayName("Obtener registro inexistente - debe lanzar excepcion")
    void obtenerPorId_noExiste_debeLanzarExcepcion() {
        when(registroRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(RegistroVehiculoNotFoundException.class, () -> registroService.obtenerPorId(9L));
    }
}
