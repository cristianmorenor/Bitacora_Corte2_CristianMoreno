package com.restaurante.controller;

import com.restaurante.mapper.PlatoEntityMapper;
import com.restaurante.mapper.PlatoMapper;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.service.PlatoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/platos")
@RequiredArgsConstructor
public class PlatoController {

    private final PlatoService platoService;
    private final PlatoMapper platoMapper;
    private final PlatoEntityMapper platoEntityMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlatoResponseDTO crear(@RequestBody @Valid PlatoRequestDTO dto) {
        Plato plato = platoMapper.toDomain(dto);
        Plato creado = platoService.crear(plato);
        return platoEntityMapper.toDTO(creado);
    }

    @GetMapping("/{id}")
    public PlatoResponseDTO obtenerPorId(@PathVariable Long id) {
        Plato plato = platoService.obtenerPorId(id);
        return platoEntityMapper.toDTO(plato);
    }

    @GetMapping
    public List<PlatoResponseDTO> obtenerTodos() {
        return platoService.obtenerTodos().stream()
                .map(platoEntityMapper::toDTO)
                .toList();
    }

    @PutMapping("/{id}")
    public PlatoResponseDTO actualizar(@PathVariable Long id, @RequestBody @Valid PlatoRequestDTO dto) {
        Plato plato = platoMapper.toDomain(dto);
        Plato actualizado = platoService.actualizar(id, plato);
        return platoEntityMapper.toDTO(actualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        platoService.eliminar(id);
    }
}