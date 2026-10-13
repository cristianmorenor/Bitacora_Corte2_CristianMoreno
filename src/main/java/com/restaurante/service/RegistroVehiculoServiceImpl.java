package com.restaurante.service;

import com.restaurante.exception.RegistroVehiculoNotFoundException;
import com.restaurante.exception.SalidaYaRegistradaException;
import com.restaurante.mapper.RegistroVehiculoPersistenceMapper;
import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.persistence.entity.RegistroVehiculoEntity;
import com.restaurante.repository.RegistroVehiculoRepository;
import com.restaurante.validator.RegistroVehiculoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistroVehiculoServiceImpl implements RegistroVehiculoService {

    private final RegistroVehiculoRepository registroRepository;
    private final RegistroVehiculoPersistenceMapper entityMapper;
    private final RegistroVehiculoValidator registroValidator;

    @Override
    @Transactional
    public RegistroVehiculo registrarEntrada(String placa) {
        String placaNormalizada = placa.toUpperCase().replace("-", "");
        registroValidator.validarSinRegistroActivo(placaNormalizada);

        RegistroVehiculo registro = new RegistroVehiculo();
        registro.setPlaca(placaNormalizada);
        registro.setEntrada(LocalDateTime.now());

        RegistroVehiculo creado = entityMapper.toDomain(registroRepository.save(entityMapper.toEntity(registro)));
        log.info("Vehiculo {} ingreso al parqueadero (registro id={})", creado.getPlaca(), creado.getId());
        return creado;
    }

    @Override
    @Transactional
    public RegistroVehiculo registrarSalida(Long id) {
        RegistroVehiculoEntity entity = buscarEntidad(id);
        RegistroVehiculo registro = entityMapper.toDomain(entity);

        if (!registro.estaActivo()) {
            throw new SalidaYaRegistradaException("El registro " + id + " ya tiene salida registrada");
        }

        registro.registrarSalida();
        entity.setSalida(registro.getSalida());
        entity.setCobro(registro.getCobro());
        registroRepository.save(entity);
        log.info("Vehiculo {} salio del parqueadero, cobro={}", registro.getPlaca(), registro.getCobro());
        return registro;
    }

    @Override
    @Transactional(readOnly = true)
    public RegistroVehiculo obtenerPorId(Long id) {
        return entityMapper.toDomain(buscarEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroVehiculo> obtenerTodos() {
        return registroRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistroVehiculo> obtenerActivos() {
        return registroRepository.findBySalidaIsNull().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!registroRepository.existsById(id)) {
            throw new RegistroVehiculoNotFoundException("Registro de vehiculo no encontrado con id: " + id);
        }
        registroRepository.deleteById(id);
        log.info("Registro de vehiculo id={} eliminado", id);
    }

    private RegistroVehiculoEntity buscarEntidad(Long id) {
        return registroRepository.findById(id)
                .orElseThrow(() -> new RegistroVehiculoNotFoundException("Registro de vehiculo no encontrado con id: " + id));
    }
}
