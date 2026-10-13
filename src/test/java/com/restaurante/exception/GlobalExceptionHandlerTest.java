package com.restaurante.exception;

import com.restaurante.controller.GlobalExceptionHandler;
import com.restaurante.model.dto.response.ErrorResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/platos/9");

    @Test
    @DisplayName("Plato no encontrado - debe responder 404 con la ruta")
    void platoNoEncontrado_debeResponder404() {
        ResponseEntity<ErrorResponseDTO> respuesta =
                handler.handleNoEncontrado(new PlatoNotFoundException("Plato no encontrado con id: 9"), request);

        assertEquals(404, respuesta.getStatusCode().value());
        assertEquals("/api/v1/platos/9", respuesta.getBody().path());
    }

    @Test
    @DisplayName("Duplicados - deben responder 409")
    void duplicado_debeResponder409() {
        assertEquals(409, handler.handleConflicto(new PlatoAlreadyExistsException("x"), request).getStatusCode().value());
        assertEquals(409, handler.handleDuplicado(new MesaAlreadyExistsException("x"), request).getStatusCode().value());
    }

    @Test
    @DisplayName("Reglas de negocio - deben responder 422")
    void reglaDeNegocio_debeResponder422() {
        assertEquals(422, handler.handleReglaDeNegocio(new PagoInsuficienteException("x"), request).getStatusCode().value());
        assertEquals(422, handler.handleTerminoCoccionRequerido(new TerminoCoccionRequeridoException("x"), request).getStatusCode().value());
    }

    @Test
    @DisplayName("Estado de pedido invalido - debe responder 400")
    void estadoInvalido_debeResponder400() {
        assertEquals(400, handler.handleEstadoInvalido(new EstadoPedidoInvalidoException("x"), request).getStatusCode().value());
    }

    @Test
    @DisplayName("Error inesperado - debe responder 500 sin exponer el detalle interno")
    void errorGeneral_debeResponder500SinDetalle() {
        ResponseEntity<ErrorResponseDTO> respuesta = handler.handleGeneral(new IllegalStateException("detalle interno"), request);

        assertEquals(500, respuesta.getStatusCode().value());
        assertFalse(respuesta.getBody().message().contains("detalle interno"));
    }
}
