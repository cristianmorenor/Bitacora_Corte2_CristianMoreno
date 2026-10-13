package com.restaurante.controller;

import com.restaurante.mapper.UsuarioMapper;
import com.restaurante.model.dto.request.UsuarioRequestDTO;
import com.restaurante.model.dto.response.UsuarioResponseDTO;
import com.restaurante.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestion del personal del restaurante (solo ADMIN)")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO crear(@RequestBody @Valid UsuarioRequestDTO dto) {
        return usuarioMapper.toDTO(usuarioService.crear(usuarioMapper.toDomain(dto)));
    }

    @GetMapping
    public List<UsuarioResponseDTO> obtenerTodos() {
        return usuarioService.obtenerTodos().stream()
                .map(usuarioMapper::toDTO)
                .toList();
    }
}
