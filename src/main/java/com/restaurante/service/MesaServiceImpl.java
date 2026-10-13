package com.restaurante.service;

import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.mapper.MesaPersistenceMapper;
import com.restaurante.model.domain.Mesa;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.repository.MesaRepository;
import com.restaurante.validator.MesaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MesaServiceImpl implements MesaService {

    private final MesaRepository mesaRepository;
    private final MesaPersistenceMapper entityMapper;
    private final MesaValidator mesaValidator;

    @Override
    @Transactional
    public Mesa crear(Mesa mesa) {
        mesaValidator.validarNumeroUnico(mesa.getNumero());

        Mesa creada = entityMapper.toDomain(mesaRepository.save(entityMapper.toEntity(mesa)));
        log.info("Mesa numero {} creada con id={}", creada.getNumero(), creada.getId());
        return creada;
    }

    @Override
    @Transactional(readOnly = true)
    public Mesa obtenerPorId(Long id) {
        return entityMapper.toDomain(buscarEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mesa> obtenerTodas() {
        return mesaRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void ocupar(Long id) {
        MesaEntity entity = buscarEntidad(id);
        Mesa mesa = entityMapper.toDomain(entity);
        mesa.ocupar();
        entity.setEstado(mesa.getEstado());
        mesaRepository.save(entity);
        log.info("Mesa id={} marcada como OCUPADA", id);
    }

    @Override
    @Transactional
    public void liberar(Long id) {
        MesaEntity entity = buscarEntidad(id);
        Mesa mesa = entityMapper.toDomain(entity);
        mesa.liberar();
        entity.setEstado(mesa.getEstado());
        mesaRepository.save(entity);
        log.info("Mesa id={} marcada como DISPONIBLE", id);
    }

    private MesaEntity buscarEntidad(Long id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new MesaNotFoundException("Mesa no encontrada con id: " + id));
    }
}
