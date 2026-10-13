package com.restaurante.validator;

import com.restaurante.exception.EstadoPedidoInvalidoException;
import com.restaurante.exception.PedidoNoModificableException;
import com.restaurante.exception.TerminoCoccionRequeridoException;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.domain.TerminoCoccion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PedidoValidatorTest {

    private final PedidoValidator validator = new PedidoValidator();
    private final Plato corte = new Plato(1L, "Picanha", 52000.0, "CORTES", true, 25, true);
    private final Plato bebida = new Plato(2L, "Limonada", 8000.0, "BEBIDAS", false, 3, true);

    @Test
    @DisplayName("Corte sin termino - debe lanzar excepcion (RN-P01)")
    void validarTerminoCoccion_corteSinTermino_debeLanzar() {
        assertThrows(TerminoCoccionRequeridoException.class, () -> validator.validarTerminoCoccion(corte, null));
    }

    @Test
    @DisplayName("Plato que no es corte - no necesita termino")
    void validarTerminoCoccion_noCorte_noDebeLanzar() {
        assertDoesNotThrow(() -> validator.validarTerminoCoccion(bebida, null));
        assertDoesNotThrow(() -> validator.validarTerminoCoccion(corte, TerminoCoccion.INGLES));
    }

    @Test
    @DisplayName("Pedido en preparacion - no se puede modificar (RN-01)")
    void validarModificable_pedidoEnPreparacion_debeLanzar() {
        Pedido pedido = new Pedido(1L, 1L, new ArrayList<>(), EstadoPedido.EN_PREPARACION, LocalDateTime.now());

        assertThrows(PedidoNoModificableException.class, () -> validator.validarModificable(pedido));
    }

    @Test
    @DisplayName("Estado escrito en minusculas - debe aceptarse")
    void validarEstado_minusculas_debeConvertir() {
        assertEquals(EstadoPedido.LISTO, validator.validarEstado("listo"));
    }

    @Test
    @DisplayName("Estado inexistente - debe lanzar excepcion")
    void validarEstado_inexistente_debeLanzar() {
        assertThrows(EstadoPedidoInvalidoException.class, () -> validator.validarEstado("QUEMADO"));
    }
}
