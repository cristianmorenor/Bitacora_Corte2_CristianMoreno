package com.restaurante.exception;

public class CuentaNotFoundException extends RuntimeException {

  public CuentaNotFoundException(String mensaje) {
    super(mensaje);
  }
}