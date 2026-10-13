package com.restaurante.controller;

import com.restaurante.mapper.RegistroVehiculoMapper;
import com.restaurante.model.dto.request.RegistroVehiculoRequestDTO;
import com.restaurante.model.dto.response.RegistroVehiculoResponseDTO;
import com.restaurante.service.RegistroVehiculoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parqueadero")
@RequiredArgsConstructor
@Tag(name = "Parqueadero", description = "Entrada, salida y cobro de vehiculos")
public class RegistroVehiculoController {

    private final RegistroVehiculoService registroService;
    private final RegistroVehiculoMapper registroMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroVehiculoResponseDTO registrarEntrada(@RequestBody @Valid RegistroVehiculoRequestDTO dto) {
        return registroMapper.toDTO(registroService.registrarEntrada(dto.placa()));
    }

    @GetMapping
    public List<RegistroVehiculoResponseDTO> obtenerTodos(@RequestParam(defaultValue = "false") boolean soloActivos) {
        return (soloActivos ? registroService.obtenerActivos() : registroService.obtenerTodos()).stream()
                .map(registroMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public RegistroVehiculoResponseDTO obtenerPorId(@PathVariable Long id) {
        return registroMapper.toDTO(registroService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/salida")
    public RegistroVehiculoResponseDTO registrarSalida(@PathVariable Long id) {
        return registroMapper.toDTO(registroService.registrarSalida(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        registroService.eliminar(id);
    }
}
