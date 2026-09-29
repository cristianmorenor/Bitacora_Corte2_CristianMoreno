package com.restaurante.service;

import com.restaurante.exception.PedidoNoModificableException;
import com.restaurante.exception.PedidoNotFoundException;
import com.restaurante.exception.TerminoCoccionRequeridoException;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PlatoService platoService;

    private final Map<Long, Pedido> pedidos = new ConcurrentHashMap<>();
    private final AtomicLong secuenciaPedido = new AtomicLong(1);
    private final AtomicLong secuenciaItem = new AtomicLong(1);

    @Override
    public Pedido crear(Long idMesa) {
        Pedido pedido = new Pedido();
        pedido.setId(secuenciaPedido.getAndIncrement());
        pedido.setIdMesa(idMesa);
        pedido.setItems(new ArrayList<>());
        pedido.setEstado(EstadoPedido.RECIBIDO);
        pedido.setConfirmadoEn(LocalDateTime.now());

        pedidos.put(pedido.getId(), pedido);
        log.info("Pedido id={} creado para mesa id={}", pedido.getId(), idMesa);
        return pedido;
    }

    @Override
    public Pedido obtenerPorId(Long id) {
        return pedidos.values().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new PedidoNotFoundException("Pedido no encontrado con id: " + id));
    }

    @Override
    public List<Pedido> obtenerTodos() {
        return pedidos.values().stream().toList();
    }

    @Override
    public Pedido agregarItem(Long idPedido, ItemPedidoRequestDTO itemDto) {
        Pedido pedido = obtenerPorId(idPedido);

        if (!pedido.puedeModificarse()) {
            throw new PedidoNoModificableException("El pedido " + idPedido + " no esta en estado RECIBIDO");
        }

        Plato plato = platoService.obtenerPorId(itemDto.idPlato());

        if (Boolean.TRUE.equals(plato.getEsCorte()) && itemDto.terminoCoccion() == null) {
            throw new TerminoCoccionRequeridoException("Todo corte debe registrar su termino de coccion");
        }

        ItemPedido item = new ItemPedido();
        item.setId(secuenciaItem.getAndIncrement());
        item.setIdPlato(plato.getId());
        item.setNombrePlato(plato.getNombre());
        item.setPrecioCongelado(plato.getPrecio());
        item.setCantidad(itemDto.cantidad());
        item.setTerminoCoccion(itemDto.terminoCoccion());
        item.setObservaciones(itemDto.observaciones());

        pedido.agregarItem(item);
        log.info("Item de plato '{}' agregado al pedido id={}", plato.getNombre(), idPedido);
        return pedido;
    }

    @Override
    public Pedido cambiarEstado(Long idPedido, String nuevoEstado) {
        Pedido pedido = obtenerPorId(idPedido);
        pedido.cambiarEstado(EstadoPedido.valueOf(nuevoEstado));
        log.info("Pedido id={} cambio a estado {}", idPedido, nuevoEstado);
        return pedido;
    }
}