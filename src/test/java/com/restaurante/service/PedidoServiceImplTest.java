package com.restaurante.service;

import com.restaurante.exception.PedidoNoModificableException;
import com.restaurante.exception.PedidoNotFoundException;
import com.restaurante.exception.TerminoCoccionRequeridoException;
import com.restaurante.model.domain.*;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private PlatoService platoService;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Plato corte;

    @BeforeEach
    void setUp() {
        corte = new Plato(1L, "Costillas BBQ", 45000.0, "PLATO_FUERTE", true, 20, true);
    }

    @Test
    @DisplayName("Crear pedido - debe quedar en estado RECIBIDO")
    void crear_pedidoNuevo_debeQuedarRecibido() {
        Pedido pedido = pedidoService.crear(1L);

        assertNotNull(pedido.getId());
        assertEquals(EstadoPedido.RECIBIDO, pedido.getEstado());
        assertTrue(pedido.getItems().isEmpty());
    }

    @Test
    @DisplayName("Obtener pedido por id inexistente - debe lanzar excepcion")
    void obtenerPorId_noExiste_debeLanzarExcepcion() {
        assertThrows(PedidoNotFoundException.class, () -> pedidoService.obtenerPorId(999L));
    }

    @Test
    @DisplayName("Agregar item con termino de coccion - debe congelar el precio (RN-04)")
    void agregarItem_corteConTermino_debeCongelarPrecio() {
        when(platoService.obtenerPorId(1L)).thenReturn(corte);
        Pedido pedido = pedidoService.crear(1L);

        ItemPedidoRequestDTO itemDto = new ItemPedidoRequestDTO(1L, 2, TerminoCoccion.TERMINO_MEDIO, null);
        Pedido actualizado = pedidoService.agregarItem(pedido.getId(), itemDto);

        assertEquals(1, actualizado.getItems().size());
        assertEquals(45000.0, actualizado.getItems().get(0).getPrecioCongelado());
    }

    @Test
    @DisplayName("Agregar corte sin termino de coccion - debe lanzar excepcion (RN-P01)")
    void agregarItem_corteSinTermino_debeLanzarExcepcion() {
        when(platoService.obtenerPorId(1L)).thenReturn(corte);
        Pedido pedido = pedidoService.crear(1L);

        ItemPedidoRequestDTO itemDto = new ItemPedidoRequestDTO(1L, 1, null, null);

        assertThrows(TerminoCoccionRequeridoException.class,
                () -> pedidoService.agregarItem(pedido.getId(), itemDto));
    }

    @Test
    @DisplayName("Agregar item a pedido no RECIBIDO - debe lanzar excepcion (RN-01)")
    void agregarItem_pedidoNoRecibido_debeLanzarExcepcion() {
        Pedido pedido = pedidoService.crear(1L);
        pedidoService.cambiarEstado(pedido.getId(), "EN_PREPARACION");

        ItemPedidoRequestDTO itemDto = new ItemPedidoRequestDTO(1L, 1, TerminoCoccion.TERMINO_MEDIO, null);

        assertThrows(PedidoNoModificableException.class,
                () -> pedidoService.agregarItem(pedido.getId(), itemDto));
    }

    @Test
    @DisplayName("Cambiar estado de pedido - debe actualizarlo correctamente")
    void cambiarEstado_pedidoValido_debeActualizar() {
        Pedido pedido = pedidoService.crear(1L);

        Pedido actualizado = pedidoService.cambiarEstado(pedido.getId(), "LISTO");

        assertEquals(EstadoPedido.LISTO, actualizado.getEstado());
    }
}