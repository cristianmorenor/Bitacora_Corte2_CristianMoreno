package com.restaurante.validator;

import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.repository.PlatoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatoValidatorTest {

    @Mock
    private PlatoRepository platoRepository;

    @InjectMocks
    private PlatoValidator platoValidator;

    @Test
    @DisplayName("Nombre nuevo - debe pasar la validacion consultando el repositorio")
    void validarNombreUnico_nombreNuevo_noDebeLanzar() {
        when(platoRepository.existsByNombreIgnoreCase("Picanha")).thenReturn(false);

        assertDoesNotThrow(() -> platoValidator.validarNombreUnico("Picanha"));
        verify(platoRepository, never()).findAll();
    }

    @Test
    @DisplayName("Nombre repetido - debe lanzar excepcion")
    void validarNombreUnico_nombreRepetido_debeLanzar() {
        when(platoRepository.existsByNombreIgnoreCase("Picanha")).thenReturn(true);

        assertThrows(PlatoAlreadyExistsException.class, () -> platoValidator.validarNombreUnico("Picanha"));
    }

    @Test
    @DisplayName("Actualizar con el nombre de otro plato - debe lanzar excepcion")
    void validarNombreUnicoAlActualizar_nombreDeOtroPlato_debeLanzar() {
        when(platoRepository.existsByNombreIgnoreCaseAndIdNot("Picanha", 2L)).thenReturn(true);

        assertThrows(PlatoAlreadyExistsException.class,
                () -> platoValidator.validarNombreUnicoAlActualizar(2L, "Picanha"));
    }
}
