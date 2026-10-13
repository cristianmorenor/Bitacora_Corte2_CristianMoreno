package com.restaurante.service;

import com.restaurante.exception.EstadoPedidoInvalidoException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.exception.PedidoNoModificableException;
import com.restaurante.exception.PedidoNotFoundException;
import com.restaurante.exception.TerminoCoccionRequeridoException;
import com.restaurante.mapper.PedidoPersistenceMapperImpl;
import com.restaurante.model.domain.*;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.restaurante.persistence.entity.CuentaEntity;
import com.restaurante.persistence.entity.ItemPedidoEntity;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.persistence.entity.PedidoEntity;
import com.restaurante.persistence.entity.PlatoEntity;
import com.restaurante.repository.CuentaRepository;
import com.restaurante.repository.MesaRepository;
import com.restaurante.repository.PedidoRepository;
import com.restaurante.repository.PlatoRepository;
import com.restaurante.validator.PedidoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private PlatoService platoService;

    @Mock
    private CuentaService cuentaService;

    @Mock
    private AuditoriaPedidoService auditoriaService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private MesaRepository mesaRepository;

    @Mock
    private PlatoRepository platoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    private PedidoServiceImpl pedidoService;

    private Plato corte;
    private MesaEntity mesa;

    @BeforeEach
    void setUp() {
        pedidoService = new PedidoServiceImpl(platoService, cuentaService, auditoriaService, pedidoRepository,
                mesaRepository, platoRepository, cuentaRepository, new PedidoPersistenceMapperImpl(), new PedidoValidator());
        corte = new Plato(1L, "Costillas BBQ", 45000.0, "PLATO_FUERTE", true, 20, true);
        mesa = new MesaEntity(1L, 1, EstadoMesa.OCUPADA);
    }

    private PedidoEntity pedidoEnEstado(EstadoPedido estado) {
        return new PedidoEntity(5L, mesa, null, new ArrayList<>(), estado, LocalDateTime.now());
    }

    @Test
    @DisplayName("Crear pedido - debe quedar en estado RECIBIDO y auditarse")
    void crear_pedidoNuevo_debeQuedarRecibido() {
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));
        when(pedidoRepository.save(any(PedidoEntity.class))).thenAnswer(inv -> {
            PedidoEntity e = inv.getArgument(0);
            e.setId(5L);
            return e;
        });

        Pedido pedido = pedidoService.crear(1L);

        assertNotNull(pedido.getId());
        assertEquals(EstadoPedido.RECIBIDO, pedido.getEstado());
        assertTrue(pedido.getItems().isEmpty());
        verify(auditoriaService).registrarCambioEstado(any(Pedido.class), isNull());
    }

    @Test
    @DisplayName("Crear pedido en mesa inexistente - debe lanzar excepcion")
    void crear_mesaNoExiste_debeLanzarExcepcion() {
        when(mesaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(MesaNotFoundException.class, () -> pedidoService.crear(99L));
    }

    @Test
    @DisplayName("Obtener pedido por id inexistente - debe lanzar excepcion")
    void obtenerPorId_noExiste_debeLanzarExcepcion() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PedidoNotFoundException.class, () -> pedidoService.obtenerPorId(999L));
    }

    @Test
    @DisplayName("Agregar item con termino de coccion - debe congelar el precio (RN-04)")
    void agregarItem_corteConTermino_debeCongelarPrecio() {
        PedidoEntity entity = pedidoEnEstado(EstadoPedido.RECIBIDO);
        when(pedidoRepository.findById(5L)).thenReturn(Optional.of(entity));
        when(platoService.obtenerPorId(1L)).thenReturn(corte);
        when(platoRepository.getReferenceById(1L)).thenReturn(new PlatoEntity(1L, "Costillas BBQ", 45000.0, "PLATO_FUERTE", true, 20, true));
        when(pedidoRepository.save(entity)).thenReturn(entity);

        ItemPedidoRequestDTO itemDto = new ItemPedidoRequestDTO(1L, 2, TerminoCoccion.TERMINO_MEDIO, null);
        Pedido actualizado = pedidoService.agregarItem(5L, itemDto);

        assertEquals(1, actualizado.getItems().size());
        assertEquals(45000.0, actualizado.getItems().get(0).getPrecioCongelado());
        assertEquals(90000.0, actualizado.calcularTotal());
    }

    @Test
    @DisplayName("Agregar corte sin termino de coccion - debe lanzar excepcion (RN-P01)")
    void agregarItem_corteSinTermino_debeLanzarExcepcion() {
        when(pedidoRepository.findById(5L)).thenReturn(Optional.of(pedidoEnEstado(EstadoPedido.RECIBIDO)));
        when(platoService.obtenerPorId(1L)).thenReturn(corte);

        ItemPedidoRequestDTO itemDto = new ItemPedidoRequestDTO(1L, 1, null, null);

        assertThrows(TerminoCoccionRequeridoException.class, () -> pedidoService.agregarItem(5L, itemDto));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Agregar item a pedido no RECIBIDO - debe lanzar excepcion (RN-01)")
    void agregarItem_pedidoNoRecibido_debeLanzarExcepcion() {
        when(pedidoRepository.findById(5L)).thenReturn(Optional.of(pedidoEnEstado(EstadoPedido.EN_PREPARACION)));

        ItemPedidoRequestDTO itemDto = new ItemPedidoRequestDTO(1L, 1, TerminoCoccion.TERMINO_MEDIO, null);

        assertThrows(PedidoNoModificableException.class, () -> pedidoService.agregarItem(5L, itemDto));
        verify(platoService, never()).obtenerPorId(any());
    }

    @Test
    @DisplayName("Cambiar estado de pedido - debe actualizarlo y registrar la auditoria")
    void cambiarEstado_pedidoValido_debeActualizar() {
        PedidoEntity entity = pedidoEnEstado(EstadoPedido.EN_PREPARACION);
        when(pedidoRepository.findById(5L)).thenReturn(Optional.of(entity));

        Pedido actualizado = pedidoService.cambiarEstado(5L, "LISTO");

        assertEquals(EstadoPedido.LISTO, actualizado.getEstado());
        assertEquals(EstadoPedido.LISTO, entity.getEstado());
        verify(auditoriaService).registrarCambioEstado(actualizado, EstadoPedido.EN_PREPARACION);
    }

    @Test
    @DisplayName("Cambiar a un estado que no existe - debe lanzar excepcion")
    void cambiarEstado_estadoInexistente_debeLanzarExcepcion() {
        assertThrows(EstadoPedidoInvalidoException.class, () -> pedidoService.cambiarEstado(5L, "QUEMADO"));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cambiar estado de un pedido ENTREGADO - debe lanzar excepcion")
    void cambiarEstado_pedidoFinalizado_debeLanzarExcepcion() {
        when(pedidoRepository.findById(5L)).thenReturn(Optional.of(pedidoEnEstado(EstadoPedido.ENTREGADO)));

        assertThrows(PedidoNoModificableException.class, () -> pedidoService.cambiarEstado(5L, "RECIBIDO"));
    }

    @Test
    @DisplayName("Entregar pedido - debe sumar su total a la cuenta abierta de la mesa")
    void cambiarEstado_entregado_debeCargarALaCuenta() {
        PedidoEntity entity = pedidoEnEstado(EstadoPedido.LISTO);
        ItemPedidoEntity item = new ItemPedidoEntity(1L, entity, new PlatoEntity(), "Costillas BBQ", 45000.0, 2,
                TerminoCoccion.TERMINO_MEDIO, null);
        entity.getItems().add(item);
        CuentaEntity cuenta = new CuentaEntity(10L, mesa, 0.0, EstadoCuenta.ABIERTA, LocalDateTime.now());
        when(pedidoRepository.findById(5L)).thenReturn(Optional.of(entity));
        when(cuentaRepository.findByMesaIdAndEstado(1L, EstadoCuenta.ABIERTA)).thenReturn(Optional.of(cuenta));

        pedidoService.cambiarEstado(5L, "ENTREGADO");

        verify(cuentaService).agregarMonto(eq(10L), eq(90000.0));
        assertEquals(cuenta, entity.getCuenta());
    }
}
