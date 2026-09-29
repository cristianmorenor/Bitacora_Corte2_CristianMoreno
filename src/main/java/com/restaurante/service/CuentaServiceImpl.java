package com.restaurante.service;

import com.restaurante.exception.CuentaCerradaException;
import com.restaurante.exception.CuentaNotFoundException;
import com.restaurante.exception.CuentaYaAbiertaException;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class CuentaServiceImpl implements CuentaService {

    private final Map<Long, Cuenta> cuentas = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(1);

    @Override
    public Cuenta abrirCuenta(Long idMesa) {
        boolean yaTieneAbierta = cuentas.values().stream()
                .anyMatch(c -> c.getIdMesa().equals(idMesa) && c.getEstado() == EstadoCuenta.ABIERTA);

        if (yaTieneAbierta) {
            log.warn("Mesa id={} ya tiene una cuenta abierta", idMesa);
            throw new CuentaYaAbiertaException("La mesa " + idMesa + " ya tiene una cuenta abierta");
        }

        Cuenta cuenta = new Cuenta();
        cuenta.setId(secuencia.getAndIncrement());
        cuenta.setIdMesa(idMesa);
        cuenta.setTotal(0.0);
        cuenta.setEstado(EstadoCuenta.ABIERTA);
        cuenta.setApertura(LocalDateTime.now());

        cuentas.put(cuenta.getId(), cuenta);
        log.info("Cuenta id={} abierta para mesa id={}", cuenta.getId(), idMesa);
        return cuenta;
    }

    @Override
    public Cuenta obtenerPorId(Long id) {
        return cuentas.values().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con id: " + id));
    }

    @Override
    public List<Cuenta> obtenerCuentasAbiertasPorMesa(Long idMesa) {
        return cuentas.values().stream()
                .filter(c -> c.getIdMesa().equals(idMesa) && c.getEstado() == EstadoCuenta.ABIERTA)
                .toList();
    }

    @Override
    public Cuenta agregarMonto(Long id, Double monto) {
        Cuenta cuenta = obtenerPorId(id);

        if (cuenta.getEstado() != EstadoCuenta.ABIERTA) {
            throw new CuentaCerradaException("La cuenta " + id + " no esta abierta, no se pueden agregar montos");
        }

        cuenta.agregarMonto(monto);
        log.info("Monto {} agregado a cuenta id={}, nuevo total={}", monto, id, cuenta.getTotal());
        return cuenta;
    }

    @Override
    public Cuenta registrarPago(Long id, Double monto) {
        Cuenta cuenta = obtenerPorId(id);

        if (cuenta.getEstado() != EstadoCuenta.ABIERTA) {
            throw new CuentaCerradaException("La cuenta " + id + " ya fue pagada o cerrada");
        }

        cuenta.registrarPago();
        cuenta.cerrarCuenta();
        log.info("Pago de {} registrado en cuenta id={}, cuenta cerrada", monto, id);
        return cuenta;
    }
}