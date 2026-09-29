package com.restaurante.exception;

public class PedidoNoModificableException extends RuntimeException {

  public PedidoNoModificableException(String mensaje) {
    super(mensaje);
  }
}