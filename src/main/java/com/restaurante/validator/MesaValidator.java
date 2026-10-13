package com.restaurante.validator;

import com.restaurante.exception.MesaAlreadyExistsException;
import com.restaurante.repository.MesaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MesaValidator {

    private final MesaRepository mesaRepository;

    public void validarNumeroUnico(Integer numero) {
        if (mesaRepository.existsByNumero(numero)) {
            log.warn("Intento de crear mesa con numero repetido: {}", numero);
            throw new MesaAlreadyExistsException("Ya existe una mesa con numero: " + numero);
        }
    }
}
