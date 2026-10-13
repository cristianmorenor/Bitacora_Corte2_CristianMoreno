package com.restaurante.service;

import com.restaurante.mapper.EventoPedidoDocumentMapper;
import com.restaurante.mapper.EventoPedidoDocumentMapperImpl;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.EventoPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.persistence.document.EventoPedidoDocument;
import com.restaurante.repository.EventoPedidoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditoriaPedidoServiceImplTest {

    @Mock
    private EventoPedidoRepository eventoPedidoRepository;

    @Spy
    private EventoPedidoDocumentMapper documentMapper = new EventoPedidoDocumentMapperImpl();

    @InjectMocks
    private AuditoriaPedidoServiceImpl auditoriaService;

    private final Pedido pedido = new Pedido(5L, 1L, new ArrayList<>(), EstadoPedido.LISTO, LocalDateTime.now());

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Registrar cambio de estado - debe guardar el documento con el usuario autenticado")
    void registrarCambioEstado_debeGuardarDocumento() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("cocina1", null, List.of()));
        ArgumentCaptor<EventoPedidoDocument> captor = ArgumentCaptor.forClass(EventoPedidoDocument.class);

        auditoriaService.registrarCambioEstado(pedido, EstadoPedido.EN_PREPARACION);

        verify(eventoPedidoRepository).save(captor.capture());
        EventoPedidoDocument guardado = captor.getValue();
        assertEquals(5L, guardado.getIdPedido());
        assertEquals(EstadoPedido.EN_PREPARACION, guardado.getEstadoAnterior());
        assertEquals(EstadoPedido.LISTO, guardado.getEstadoNuevo());
        assertEquals("cocina1", guardado.getUsuario());
        assertNotNull(guardado.getFecha());
    }

    @Test
    @DisplayName("Si Mongo falla - la auditoria no debe tumbar la operacion del pedido")
    void registrarCambioEstado_mongoCaido_noDebePropagarError() {
        when(eventoPedidoRepository.save(any())).thenThrow(new RuntimeException("Mongo no disponible"));

        assertDoesNotThrow(() -> auditoriaService.registrarCambioEstado(pedido, EstadoPedido.EN_PREPARACION));
    }

    @Test
    @DisplayName("Obtener historial - debe traer los eventos del pedido en orden")
    void obtenerHistorial_debeMapearEventos() {
        EventoPedidoDocument doc = new EventoPedidoDocument("abc", 5L, 1L, null, EstadoPedido.RECIBIDO, "mesero1", LocalDateTime.now());
        when(eventoPedidoRepository.findByIdPedidoOrderByFechaAsc(5L)).thenReturn(List.of(doc));

        List<EventoPedido> historial = auditoriaService.obtenerHistorial(5L);

        assertEquals(1, historial.size());
        assertEquals(EstadoPedido.RECIBIDO, historial.get(0).getEstadoNuevo());
        assertEquals("mesero1", historial.get(0).getUsuario());
    }
}
