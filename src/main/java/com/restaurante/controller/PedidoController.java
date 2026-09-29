package com.restaurante.controller;

import com.restaurante.mapper.PedidoMapper;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.service.PedidoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "El flujo de cocina: crear pedido, agregar items, cambiar estado")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoMapper pedidoMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponseDTO crear(@RequestBody @Valid PedidoRequestDTO dto) {
        Pedido pedido = pedidoService.crear(dto.idMesa());
        return pedidoMapper.toDTO(pedido);
    }

    @GetMapping
    public List<PedidoResponseDTO> obtenerTodos() {
        return pedidoService.obtenerTodos().stream()
                .map(pedidoMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public PedidoResponseDTO obtenerPorId(@PathVariable Long id) {
        Pedido pedido = pedidoService.obtenerPorId(id);
        return pedidoMapper.toDTO(pedido);
    }

    @PostMapping("/{id}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponseDTO agregarItem(@PathVariable Long id, @RequestBody @Valid ItemPedidoRequestDTO itemDto) {
        Pedido pedido = pedidoService.agregarItem(id, itemDto);
        return pedidoMapper.toDTO(pedido);
    }

    @PatchMapping("/{id}/estado")
    public PedidoResponseDTO cambiarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        Pedido pedido = pedidoService.cambiarEstado(id, nuevoEstado);
        return pedidoMapper.toDTO(pedido);
    }
}