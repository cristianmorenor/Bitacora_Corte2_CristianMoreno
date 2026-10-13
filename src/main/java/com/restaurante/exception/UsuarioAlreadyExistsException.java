package com.restaurante.exception;

public class UsuarioAlreadyExistsException extends RuntimeException {

    public UsuarioAlreadyExistsException(String mensaje) {
        super(mensaje);
    }
}
