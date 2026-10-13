package com.restaurante.service;

import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.exception.PedidoNoModificableException;
import com.restaurante.exception.PedidoNotFoundException;
import com.restaurante.mapper.PedidoPersistenceMapper;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.EventoPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.restaurante.persistence.entity.ItemPedidoEntity;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.persistence.entity.PedidoEntity;
import com.restaurante.repository.CuentaRepository;
import com.restaurante.repository.MesaRepository;
import com.restaurante.repository.PedidoRepository;
import com.restaurante.repository.PlatoRepository;
import com.restaurante.validator.PedidoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PlatoService platoService;
    private final CuentaService cuentaService;
    private final AuditoriaPedidoService auditoriaService;
    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final PlatoRepository platoRepository;
    private final CuentaRepository cuentaRepository;
    private final PedidoPersistenceMapper entityMapper;
    private final PedidoValidator pedidoValidator;

    @Override
    @Transactional
    public Pedido crear(Long idMesa) {
        MesaEntity mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new MesaNotFoundException("Mesa no encontrada con id: " + idMesa));

        PedidoEntity entity = new PedidoEntity();
        entity.setMesa(mesa);
        entity.setEstado(EstadoPedido.RECIBIDO);
        entity.setConfirmadoEn(LocalDateTime.now());

        Pedido pedido = entityMapper.toDomain(pedidoRepository.save(entity));
        auditoriaService.registrarCambioEstado(pedido, null);
        log.info("Pedido id={} creado para mesa id={}", pedido.getId(), idMesa);
        return pedido;
    }

    @Override
    @Transactional(readOnly = true)
    public Pedido obtenerPorId(Long id) {
        return entityMapper.toDomain(buscarEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAllConItems().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public Pedido agregarItem(Long idPedido, ItemPedidoRequestDTO itemDto) {
        PedidoEntity entity = buscarEntidad(idPedido);
        pedidoValidator.validarModificable(entityMapper.toDomain(entity));

        Plato plato = platoService.obtenerPorId(itemDto.idPlato());
        pedidoValidator.validarTerminoCoccion(plato, itemDto.terminoCoccion());

        ItemPedidoEntity item = new ItemPedidoEntity();
        item.setPlato(platoRepository.getReferenceById(plato.getId()));
        item.setNombrePlato(plato.getNombre());
        item.setPrecioCongelado(plato.getPrecio());
        item.setCantidad(itemDto.cantidad());
        item.setTerminoCoccion(itemDto.terminoCoccion());
        item.setObservaciones(itemDto.observaciones());
        entity.agregarItem(item);

        Pedido pedido = entityMapper.toDomain(pedidoRepository.save(entity));
        log.info("Item de plato '{}' agregado al pedido id={}", plato.getNombre(), idPedido);
        return pedido;
    }

    @Override
    @Transactional
    public Pedido cambiarEstado(Long idPedido, String nuevoEstado) {
        EstadoPedido estado = pedidoValidator.validarEstado(nuevoEstado);
        PedidoEntity entity = buscarEntidad(idPedido);
        Pedido pedido = entityMapper.toDomain(entity);

        if (pedido.estaFinalizado()) {
            throw new PedidoNoModificableException("El pedido " + idPedido + " ya fue " + pedido.getEstado()
                    + " y no puede cambiar de estado");
        }

        EstadoPedido estadoAnterior = pedido.getEstado();
        pedido.cambiarEstado(estado);
        entity.setEstado(estado);

        if (estado == EstadoPedido.ENTREGADO) {
            cargarACuentaDeLaMesa(entity, pedido);
        }

        pedidoRepository.save(entity);
        auditoriaService.registrarCambioEstado(pedido, estadoAnterior);
        log.info("Pedido id={} cambio de {} a {}", idPedido, estadoAnterior, estado);
        return pedido;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoPedido> obtenerHistorial(Long idPedido) {
        buscarEntidad(idPedido);
        return auditoriaService.obtenerHistorial(idPedido);
    }

    // Al entregar un pedido su total se suma a la cuenta abierta de la mesa, si existe
    private void cargarACuentaDeLaMesa(PedidoEntity entity, Pedido pedido) {
        cuentaRepository.findByMesaIdAndEstado(pedido.getIdMesa(), EstadoCuenta.ABIERTA)
                .ifPresent(cuenta -> {
                    entity.setCuenta(cuenta);
                    cuentaService.agregarMonto(cuenta.getId(), pedido.calcularTotal());
                });
    }

    private PedidoEntity buscarEntidad(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException("Pedido no encontrado con id: " + id));
    }
}
