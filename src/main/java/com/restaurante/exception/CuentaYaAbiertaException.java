package com.restaurante.exception;

public class CuentaYaAbiertaException extends RuntimeException {

  public CuentaYaAbiertaException(String mensaje) {
    super(mensaje);
  }
}