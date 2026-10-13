package com.restaurante.service;

import com.restaurante.exception.CuentaCerradaException;
import com.restaurante.exception.CuentaNotFoundException;
import com.restaurante.exception.CuentaYaAbiertaException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.exception.PagoInsuficienteException;
import com.restaurante.mapper.CuentaPersistenceMapper;
import com.restaurante.mapper.CuentaPersistenceMapperImpl;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.persistence.entity.CuentaEntity;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.repository.CuentaRepository;
import com.restaurante.repository.MesaRepository;
import com.restaurante.validator.CuentaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class CuentaServiceImplTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private MesaRepository mesaRepository;

    @Spy
    private CuentaPersistenceMapper entityMapper = new CuentaPersistenceMapperImpl();

    private CuentaServiceImpl cuentaService;

    private MesaEntity mesa;

    @BeforeEach
    void setUp() {
        // Validador real sobre el repositorio mockeado, asi se prueban las reglas RN-03 y de pago
        CuentaValidator validator = new CuentaValidator(cuentaRepository);
        cuentaService = new CuentaServiceImpl(cuentaRepository, mesaRepository, entityMapper, validator);
        mesa = new MesaEntity(1L, 1, EstadoMesa.OCUPADA);
    }

    private CuentaEntity cuentaAbierta(Double total) {
        return new CuentaEntity(10L, mesa, total, EstadoCuenta.ABIERTA, LocalDateTime.now());
    }

    @Test
    @DisplayName("Abrir cuenta - debe crear cuenta en estado ABIERTA")
    void abrirCuenta_mesaSinCuenta_debeCrearla() {
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));
        when(cuentaRepository.existsByMesaIdAndEstado(1L, EstadoCuenta.ABIERTA)).thenReturn(false);
        when(cuentaRepository.save(any(CuentaEntity.class))).thenAnswer(inv -> {
            CuentaEntity e = inv.getArgument(0);
            e.setId(10L);
            return e;
        });

        Cuenta cuenta = cuentaService.abrirCuenta(1L);

        assertNotNull(cuenta.getId());
        assertEquals(1L, cuenta.getIdMesa());
        assertEquals(EstadoCuenta.ABIERTA, cuenta.getEstado());
        assertEquals(0.0, cuenta.getTotal());
    }

    @Test
    @DisplayName("Abrir cuenta cuando ya hay una abierta - debe lanzar excepcion (RN-03)")
    void abrirCuenta_mesaYaTieneAbierta_debeLanzarExcepcion() {
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));
        when(cuentaRepository.existsByMesaIdAndEstado(1L, EstadoCuenta.ABIERTA)).thenReturn(true);

        assertThrows(CuentaYaAbiertaException.class, () -> cuentaService.abrirCuenta(1L));
        verify(cuentaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Abrir cuenta en mesa inexistente - debe lanzar excepcion")
    void abrirCuenta_mesaNoExiste_debeLanzarExcepcion() {
        when(mesaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(MesaNotFoundException.class, () -> cuentaService.abrirCuenta(99L));
    }

    @Test
    @DisplayName("Obtener cuenta por id inexistente - debe lanzar excepcion")
    void obtenerPorId_noExiste_debeLanzarExcepcion() {
        when(cuentaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFoundException.class, () -> cuentaService.obtenerPorId(999L));
    }

    @Test
    @DisplayName("Obtener cuentas abiertas de mesa sin cuentas - debe retornar lista vacia")
    void obtenerCuentasAbiertas_sinCuentas_debeRetornarListaVacia() {
        when(cuentaRepository.findByMesaIdAndEstado(1L, EstadoCuenta.ABIERTA)).thenReturn(Optional.empty());

        List<Cuenta> resultado = cuentaService.obtenerCuentasAbiertasPorMesa(1L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Agregar monto a cuenta abierta - debe sumar al total")
    void agregarMonto_cuentaAbierta_debeSumarTotal() {
        CuentaEntity entity = cuentaAbierta(0.0);
        when(cuentaRepository.findById(10L)).thenReturn(Optional.of(entity));

        Cuenta actualizada = cuentaService.agregarMonto(10L, 45000.0);

        assertEquals(45000.0, actualizada.getTotal());
        assertEquals(45000.0, entity.getTotal());
        verify(cuentaRepository).save(entity);
    }

    @Test
    @DisplayName("Registrar pago - debe cerrar la cuenta")
    void registrarPago_cuentaAbierta_debeCerrarla() {
        CuentaEntity entity = cuentaAbierta(45000.0);
        when(cuentaRepository.findById(10L)).thenReturn(Optional.of(entity));

        Cuenta pagada = cuentaService.registrarPago(10L, 45000.0);

        assertEquals(EstadoCuenta.CERRADA, pagada.getEstado());
        assertEquals(EstadoCuenta.CERRADA, entity.getEstado());
    }

    @Test
    @DisplayName("Registrar pago menor al total - debe lanzar excepcion")
    void registrarPago_montoInsuficiente_debeLanzarExcepcion() {
        when(cuentaRepository.findById(10L)).thenReturn(Optional.of(cuentaAbierta(45000.0)));

        assertThrows(PagoInsuficienteException.class, () -> cuentaService.registrarPago(10L, 10000.0));
        verify(cuentaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Registrar pago en cuenta ya cerrada - debe lanzar excepcion")
    void registrarPago_cuentaYaCerrada_debeLanzarExcepcion() {
        CuentaEntity cerrada = cuentaAbierta(45000.0);
        cerrada.setEstado(EstadoCuenta.CERRADA);
        when(cuentaRepository.findById(10L)).thenReturn(Optional.of(cerrada));

        assertThrows(CuentaCerradaException.class, () -> cuentaService.registrarPago(10L, 10000.0));
    }
}
