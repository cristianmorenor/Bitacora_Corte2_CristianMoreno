package com.restaurante.validator;

import com.restaurante.exception.CuentaCerradaException;
import com.restaurante.exception.CuentaYaAbiertaException;
import com.restaurante.exception.PagoInsuficienteException;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CuentaValidator {

    private final CuentaRepository cuentaRepository;

    // RN-03: una mesa solo puede tener una cuenta abierta
    public void validarMesaSinCuentaAbierta(Long idMesa) {
        if (cuentaRepository.existsByMesaIdAndEstado(idMesa, EstadoCuenta.ABIERTA)) {
            log.warn("Mesa id={} ya tiene una cuenta abierta", idMesa);
            throw new CuentaYaAbiertaException("La mesa " + idMesa + " ya tiene una cuenta abierta");
        }
    }

    public void validarCuentaAbierta(Cuenta cuenta, String mensaje) {
        if (cuenta.getEstado() != EstadoCuenta.ABIERTA) {
            throw new CuentaCerradaException(mensaje);
        }
    }

    public void validarMontoCubreTotal(Cuenta cuenta, Double monto) {
        if (monto < cuenta.calcularTotal()) {
            throw new PagoInsuficienteException("El pago de " + monto
                    + " no cubre el total de la cuenta (" + cuenta.calcularTotal() + ")");
        }
    }
}
