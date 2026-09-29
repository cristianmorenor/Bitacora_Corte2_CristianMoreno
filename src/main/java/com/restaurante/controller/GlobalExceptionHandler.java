package com.restaurante.controller;

import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.model.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidacion(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(" | "));

        log.warn("Validacion de input fallida: {}", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.of(400, "Datos de entrada invalidos", errores, request.getRequestURI()));
    }

    @ExceptionHandler(PlatoNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoEncontrado(
            PlatoNotFoundException ex, HttpServletRequest request) {

        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseDTO.of(404, "No encontrado", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(PlatoAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflicto(
            PlatoAlreadyExistsException ex, HttpServletRequest request) {

        log.warn("Conflicto de negocio: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponseDTO.of(409, "Conflicto", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneral(
            Exception ex, HttpServletRequest request) {

        log.error("Error no controlado: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDTO.of(500, "Error interno", "Algo salio mal, intenta de nuevo", request.getRequestURI()));
    }
}