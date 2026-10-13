package com.restaurante.service;

import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.mapper.PlatoPersistenceMapper;
import com.restaurante.model.domain.Plato;
import com.restaurante.persistence.entity.PlatoEntity;
import com.restaurante.repository.PlatoRepository;
import com.restaurante.validator.PlatoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatoServiceImpl implements PlatoService {

    private final PlatoRepository platoRepository;
    private final PlatoPersistenceMapper entityMapper;
    private final PlatoValidator platoValidator;

    @Override
    @Transactional
    public Plato crear(Plato plato) {
        platoValidator.validarNombreUnico(plato.getNombre());

        Plato guardado = entityMapper.toDomain(platoRepository.save(entityMapper.toEntity(plato)));
        log.info("Plato '{}' creado con id={}", guardado.getNombre(), guardado.getId());
        return guardado;
    }

    @Override
    @Transactional(readOnly = true)
    public Plato obtenerPorId(Long id) {
        return platoRepository.findById(id)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> new PlatoNotFoundException("Plato no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Plato> obtenerTodos() {
        return platoRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Plato> obtenerDisponibles() {
        return platoRepository.findByActivoTrue().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public Plato actualizar(Long id, Plato platoActualizado) {
        PlatoEntity existente = platoRepository.findById(id)
                .orElseThrow(() -> new PlatoNotFoundException("Plato no encontrado con id: " + id));

        platoValidator.validarNombreUnicoAlActualizar(id, platoActualizado.getNombre());

        existente.setNombre(platoActualizado.getNombre());
        existente.setPrecio(platoActualizado.getPrecio());
        existente.setCategoria(platoActualizado.getCategoria());
        existente.setEsCorte(platoActualizado.getEsCorte());
        existente.setMinutosPreparacion(platoActualizado.getMinutosPreparacion());

        log.info("Plato con id={} actualizado", id);
        return entityMapper.toDomain(platoRepository.save(existente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!platoRepository.existsById(id)) {
            throw new PlatoNotFoundException("Plato no encontrado con id: " + id);
        }
        platoRepository.deleteById(id);
        log.info("Plato con id={} eliminado", id);
    }
}
