package com.restaurante.exception;

public class EstadoPedidoInvalidoException extends RuntimeException {

    public EstadoPedidoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
