package com.restaurante.controller;

import com.restaurante.mapper.ReservaMapper;
import com.restaurante.model.domain.Reserva;
import com.restaurante.model.dto.request.ReprogramarReservaRequestDTO;
import com.restaurante.model.dto.request.ReservaRequestDTO;
import com.restaurante.model.dto.response.ReservaResponseDTO;
import com.restaurante.service.ReservaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Reservas de mesas del restaurante")
public class ReservaController {

    private final ReservaService reservaService;
    private final ReservaMapper reservaMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaResponseDTO crear(@RequestBody @Valid ReservaRequestDTO dto) {
        Reserva creada = reservaService.crear(reservaMapper.toDomain(dto));
        return reservaMapper.toDTO(creada);
    }

    @GetMapping
    public List<ReservaResponseDTO> obtenerTodas(@RequestParam(required = false) Long idMesa) {
        List<Reserva> reservas = idMesa != null
                ? reservaService.obtenerPorMesa(idMesa)
                : reservaService.obtenerTodas();
        return reservas.stream()
                .map(reservaMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ReservaResponseDTO obtenerPorId(@PathVariable Long id) {
        return reservaMapper.toDTO(reservaService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/reprogramar")
    public ReservaResponseDTO reprogramar(@PathVariable Long id, @RequestBody @Valid ReprogramarReservaRequestDTO dto) {
        return reservaMapper.toDTO(reservaService.reprogramar(id, dto.fechaHora()));
    }

    @PatchMapping("/{id}/cancelar")
    public ReservaResponseDTO cancelar(@PathVariable Long id) {
        return reservaMapper.toDTO(reservaService.cancelar(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
    }
}
