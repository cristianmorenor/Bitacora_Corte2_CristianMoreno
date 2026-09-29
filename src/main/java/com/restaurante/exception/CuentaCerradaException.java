package com.restaurante.exception;

public class CuentaCerradaException extends RuntimeException {

  public CuentaCerradaException(String mensaje) {
    super(mensaje);
  }
}