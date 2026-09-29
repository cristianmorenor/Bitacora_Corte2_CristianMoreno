package com.restaurante.exception;

public class TerminoCoccionRequeridoException extends RuntimeException {

  public TerminoCoccionRequeridoException(String mensaje) {
    super(mensaje);
  }
}