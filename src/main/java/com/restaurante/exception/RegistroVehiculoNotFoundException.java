package com.restaurante.exception;

public class RegistroVehiculoNotFoundException extends RuntimeException {

    public RegistroVehiculoNotFoundException(String mensaje) {
        super(mensaje);
    }
}
