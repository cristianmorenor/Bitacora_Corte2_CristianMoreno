package com.restaurante.service;

import com.restaurante.mapper.EventoPedidoDocumentMapper;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.EventoPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.repository.EventoPedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditoriaPedidoServiceImpl implements AuditoriaPedidoService {

    private final EventoPedidoRepository eventoPedidoRepository;
    private final EventoPedidoDocumentMapper documentMapper;

    @Override
    public void registrarCambioEstado(Pedido pedido, EstadoPedido estadoAnterior) {
        EventoPedido evento = new EventoPedido(null, pedido.getId(), pedido.getIdMesa(),
                estadoAnterior, pedido.getEstado(), usuarioActual(), LocalDateTime.now());
        try {
            eventoPedidoRepository.save(documentMapper.toDocument(evento));
            log.info("Auditoria: pedido id={} paso de {} a {}", pedido.getId(), estadoAnterior, pedido.getEstado());
        } catch (RuntimeException ex) {
            // La auditoria no debe tumbar la operacion principal del restaurante
            log.error("No se pudo registrar la auditoria del pedido id={}: {}", pedido.getId(), ex.getMessage());
        }
    }

    @Override
    public List<EventoPedido> obtenerHistorial(Long idPedido) {
        return eventoPedidoRepository.findByIdPedidoOrderByFechaAsc(idPedido).stream()
                .map(documentMapper::toDomain)
                .toList();
    }

    private String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "sistema";
    }
}
