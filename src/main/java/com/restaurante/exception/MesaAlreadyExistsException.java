package com.restaurante.exception;

public class MesaAlreadyExistsException extends RuntimeException {

    public MesaAlreadyExistsException(String mensaje) {
        super(mensaje);
    }
}
