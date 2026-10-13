package com.restaurante.exception;

public class SalidaYaRegistradaException extends RuntimeException {

    public SalidaYaRegistradaException(String mensaje) {
        super(mensaje);
    }
}
