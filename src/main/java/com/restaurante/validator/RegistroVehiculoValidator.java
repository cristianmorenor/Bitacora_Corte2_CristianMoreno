package com.restaurante.validator;

import com.restaurante.exception.VehiculoYaRegistradoException;
import com.restaurante.repository.RegistroVehiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistroVehiculoValidator {

    private final RegistroVehiculoRepository registroVehiculoRepository;

    public void validarSinRegistroActivo(String placa) {
        if (registroVehiculoRepository.existsByPlacaIgnoreCaseAndSalidaIsNull(placa)) {
            log.warn("El vehiculo con placa {} ya esta dentro del parqueadero", placa);
            throw new VehiculoYaRegistradoException("El vehiculo con placa " + placa + " ya esta en el parqueadero");
        }
    }
}
