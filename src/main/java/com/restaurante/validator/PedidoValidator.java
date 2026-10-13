package com.restaurante.validator;

import com.restaurante.exception.EstadoPedidoInvalidoException;
import com.restaurante.exception.PedidoNoModificableException;
import com.restaurante.exception.TerminoCoccionRequeridoException;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.domain.TerminoCoccion;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class PedidoValidator {

    // RN-01: solo se modifican pedidos en estado RECIBIDO
    public void validarModificable(Pedido pedido) {
        if (!pedido.puedeModificarse()) {
            throw new PedidoNoModificableException("El pedido " + pedido.getId() + " no esta en estado RECIBIDO");
        }
    }

    // RN-P01: todo corte debe registrar su termino de coccion
    public void validarTerminoCoccion(Plato plato, TerminoCoccion terminoCoccion) {
        if (Boolean.TRUE.equals(plato.getEsCorte()) && terminoCoccion == null) {
            throw new TerminoCoccionRequeridoException("Todo corte debe registrar su termino de coccion");
        }
    }

    public EstadoPedido validarEstado(String estado) {
        return Arrays.stream(EstadoPedido.values())
                .filter(e -> e.name().equalsIgnoreCase(estado))
                .findFirst()
                .orElseThrow(() -> new EstadoPedidoInvalidoException("Estado de pedido invalido: " + estado
                        + ". Valores permitidos: " + Arrays.toString(EstadoPedido.values())));
    }
}
