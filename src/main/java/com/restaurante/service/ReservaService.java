package com.restaurante.service;

import com.restaurante.model.domain.Reserva;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaService {

    Reserva crear(Reserva reserva);

    Reserva obtenerPorId(Long id);

    List<Reserva> obtenerTodas();

    List<Reserva> obtenerPorMesa(Long idMesa);

    Reserva reprogramar(Long id, LocalDateTime nuevaFechaHora);

    Reserva cancelar(Long id);

    void eliminar(Long id);
}
