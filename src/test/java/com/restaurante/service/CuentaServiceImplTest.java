package com.restaurante.service;

import com.restaurante.exception.CuentaCerradaException;
import com.restaurante.exception.CuentaNotFoundException;
import com.restaurante.exception.CuentaYaAbiertaException;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {

    private CuentaServiceImpl cuentaService;

    @BeforeEach
    void setUp() {
        cuentaService = new CuentaServiceImpl();
    }

    @Test
    @DisplayName("Abrir cuenta - debe crear cuenta en estado ABIERTA")
    void abrirCuenta_mesaSinCuenta_debeCrearla() {
        Cuenta cuenta = cuentaService.abrirCuenta(1L);

        assertNotNull(cuenta.getId());
        assertEquals(EstadoCuenta.ABIERTA, cuenta.getEstado());
        assertEquals(0.0, cuenta.getTotal());
    }

    @Test
    @DisplayName("Abrir cuenta cuando ya hay una abierta - debe lanzar excepcion (RN-03)")
    void abrirCuenta_mesaYaTieneAbierta_debeLanzarExcepcion() {
        cuentaService.abrirCuenta(1L);

        assertThrows(CuentaYaAbiertaException.class, () -> cuentaService.abrirCuenta(1L));
    }

    @Test
    @DisplayName("Obtener cuenta por id inexistente - debe lanzar excepcion")
    void obtenerPorId_noExiste_debeLanzarExcepcion() {
        assertThrows(CuentaNotFoundException.class, () -> cuentaService.obtenerPorId(999L));
    }

    @Test
    @DisplayName("Obtener cuentas abiertas de mesa sin cuentas - debe retornar lista vacia")
    void obtenerCuentasAbiertas_sinCuentas_debeRetornarListaVacia() {
        List<Cuenta> resultado = cuentaService.obtenerCuentasAbiertasPorMesa(1L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Agregar monto a cuenta abierta - debe sumar al total")
    void agregarMonto_cuentaAbierta_debeSumarTotal() {
        Cuenta cuenta = cuentaService.abrirCuenta(1L);

        Cuenta actualizada = cuentaService.agregarMonto(cuenta.getId(), 45000.0);

        assertEquals(45000.0, actualizada.getTotal());
    }

    @Test
    @DisplayName("Registrar pago - debe cerrar la cuenta")
    void registrarPago_cuentaAbierta_debeCerrarla() {
        Cuenta cuenta = cuentaService.abrirCuenta(1L);

        Cuenta pagada = cuentaService.registrarPago(cuenta.getId(), 45000.0);

        assertEquals(EstadoCuenta.CERRADA, pagada.getEstado());
    }

    @Test
    @DisplayName("Registrar pago en cuenta ya cerrada - debe lanzar excepcion")
    void registrarPago_cuentaYaCerrada_debeLanzarExcepcion() {
        Cuenta cuenta = cuentaService.abrirCuenta(1L);
        cuentaService.registrarPago(cuenta.getId(), 45000.0);

        assertThrows(CuentaCerradaException.class,
                () -> cuentaService.registrarPago(cuenta.getId(), 10000.0));
    }
}