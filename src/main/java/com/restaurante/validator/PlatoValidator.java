package com.restaurante.validator;

import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.repository.PlatoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlatoValidator {

    private final PlatoRepository platoRepository;

    public void validarNombreUnico(String nombre) {
        if (platoRepository.existsByNombreIgnoreCase(nombre)) {
            log.warn("Intento de crear plato duplicado: '{}'", nombre);
            throw new PlatoAlreadyExistsException("Ya existe un plato con nombre: " + nombre);
        }
    }

    public void validarNombreUnicoAlActualizar(Long id, String nombre) {
        if (platoRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            log.warn("Intento de renombrar plato id={} a un nombre existente: '{}'", id, nombre);
            throw new PlatoAlreadyExistsException("Ya existe un plato con nombre: " + nombre);
        }
    }
}
