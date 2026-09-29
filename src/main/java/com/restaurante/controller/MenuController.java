package com.restaurante.controller;

import com.restaurante.mapper.PlatoEntityMapper;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.service.PlatoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
@Tag(name = "Menu", description = "Consulta de la carta disponible (cliente)")
public class MenuController {

    private final PlatoService platoService;
    private final PlatoEntityMapper platoEntityMapper;

    @GetMapping
    public List<PlatoResponseDTO> obtenerMenu() {
        return platoService.obtenerDisponibles().stream()
                .map(platoEntityMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public PlatoResponseDTO obtenerPlatoDelMenu(@PathVariable Long id) {
        Plato plato = platoService.obtenerPorId(id);
        return platoEntityMapper.toDTO(plato);
    }
}