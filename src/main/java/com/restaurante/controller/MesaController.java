package com.restaurante.controller;

import com.restaurante.mapper.MesaMapper;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;
import com.restaurante.service.MesaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mesas")
@RequiredArgsConstructor
@Tag(name = "Mesas", description = "Gestión del salón del restaurante")
public class MesaController {

    private final MesaService mesaService;
    private final MesaMapper mesaMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MesaResponseDTO crear(@RequestBody @Valid MesaRequestDTO dto) {
        Mesa mesa = mesaMapper.toDomain(dto);
        Mesa creada = mesaService.crear(mesa);
        return mesaMapper.toDTO(creada);
    }

    @GetMapping
    public List<MesaResponseDTO> obtenerTodas() {
        return mesaService.obtenerTodas().stream()
                .map(mesaMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public MesaResponseDTO obtenerPorId(@PathVariable Long id) {
        Mesa mesa = mesaService.obtenerPorId(id);
        return mesaMapper.toDTO(mesa);
    }

    @PatchMapping("/{id}/ocupar")
    public void ocupar(@PathVariable Long id) {
        mesaService.ocupar(id);
    }

    @PatchMapping("/{id}/liberar")
    public void liberar(@PathVariable Long id) {
        mesaService.liberar(id);
    }
}