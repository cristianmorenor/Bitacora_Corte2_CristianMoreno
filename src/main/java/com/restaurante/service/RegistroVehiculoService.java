package com.restaurante.service;

import com.restaurante.model.domain.RegistroVehiculo;

import java.util.List;

public interface RegistroVehiculoService {

    RegistroVehiculo registrarEntrada(String placa);

    RegistroVehiculo registrarSalida(Long id);

    RegistroVehiculo obtenerPorId(Long id);

    List<RegistroVehiculo> obtenerTodos();

    List<RegistroVehiculo> obtenerActivos();

    void eliminar(Long id);
}
