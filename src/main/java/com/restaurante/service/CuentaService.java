package com.restaurante.service;

import com.restaurante.model.domain.Cuenta;

import java.util.List;

public interface CuentaService {

    Cuenta abrirCuenta(Long idMesa);

    Cuenta obtenerPorId(Long id);

    List<Cuenta> obtenerCuentasAbiertasPorMesa(Long idMesa);

    Cuenta agregarMonto(Long id, Double monto);

    Cuenta registrarPago(Long id, Double monto);
}