package com.restaurante.controller;

import com.restaurante.mapper.CuentaMapper;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.dto.request.PagoRequestDTO;
import com.restaurante.model.dto.response.CuentaResponseDTO;
import com.restaurante.service.CuentaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Cuenta y Pago", description = "Cierre del ciclo: cuenta abierta, montos y pago")
public class CuentaController {

    private final CuentaService cuentaService;
    private final CuentaMapper cuentaMapper;

    @PostMapping("/api/v1/mesas/{idMesa}/cuentas")
    @ResponseStatus(HttpStatus.CREATED)
    public CuentaResponseDTO abrirCuenta(@PathVariable Long idMesa) {
        Cuenta cuenta = cuentaService.abrirCuenta(idMesa);
        return cuentaMapper.toDTO(cuenta);
    }

    @GetMapping("/api/v1/mesas/{idMesa}/cuentas")
    public List<CuentaResponseDTO> obtenerCuentasAbiertas(@PathVariable Long idMesa,
                                                          @RequestParam(required = false) Boolean estado) {
        return cuentaService.obtenerCuentasAbiertasPorMesa(idMesa).stream()
                .map(cuentaMapper::toDTO)
                .toList();
    }

    @GetMapping("/api/v1/cuentas/{id}")
    public CuentaResponseDTO obtenerPorId(@PathVariable Long id) {
        Cuenta cuenta = cuentaService.obtenerPorId(id);
        return cuentaMapper.toDTO(cuenta);
    }

    @PostMapping("/api/v1/cuentas/{id}/pagos")
    @ResponseStatus(HttpStatus.CREATED)
    public CuentaResponseDTO registrarPago(@PathVariable Long id, @RequestBody @Valid PagoRequestDTO dto) {
        Cuenta cuenta = cuentaService.registrarPago(id, dto.monto());
        return cuentaMapper.toDTO(cuenta);
    }
}