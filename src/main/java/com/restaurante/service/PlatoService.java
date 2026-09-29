package com.restaurante.service;

import com.restaurante.model.domain.Plato;

import java.util.List;

public interface PlatoService {

    Plato crear(Plato plato);

    Plato obtenerPorId(Long id);

    List<Plato> obtenerTodos();

    List<Plato> obtenerDisponibles();

    Plato actualizar(Long id, Plato plato);

    void eliminar(Long id);
}