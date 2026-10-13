package com.restaurante.service;

import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.exception.ReservaNotFoundException;
import com.restaurante.mapper.ReservaPersistenceMapper;
import com.restaurante.model.domain.Reserva;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.persistence.entity.ReservaEntity;
import com.restaurante.repository.MesaRepository;
import com.restaurante.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final MesaRepository mesaRepository;
    private final ReservaPersistenceMapper entityMapper;

    @Override
    @Transactional
    public Reserva crear(Reserva reserva) {
        MesaEntity mesa = mesaRepository.findById(reserva.getIdMesa())
                .orElseThrow(() -> new MesaNotFoundException("Mesa no encontrada con id: " + reserva.getIdMesa()));

        ReservaEntity entity = entityMapper.toEntity(reserva);
        entity.setMesa(mesa);

        Reserva creada = entityMapper.toDomain(reservaRepository.save(entity));
        log.info("Reserva id={} creada para mesa id={} a nombre de '{}'", creada.getId(), creada.getIdMesa(), creada.getCliente());
        return creada;
    }

    @Override
    @Transactional(readOnly = true)
    public Reserva obtenerPorId(Long id) {
        return entityMapper.toDomain(buscarEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> obtenerPorMesa(Long idMesa) {
        return reservaRepository.findByMesaId(idMesa).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public Reserva reprogramar(Long id, LocalDateTime nuevaFechaHora) {
        ReservaEntity entity = buscarEntidad(id);
        Reserva reserva = entityMapper.toDomain(entity);
        reserva.reprogramar(nuevaFechaHora);
        entity.setFechaHora(reserva.getFechaHora());
        reservaRepository.save(entity);
        log.info("Reserva id={} reprogramada para {}", id, nuevaFechaHora);
        return reserva;
    }

    @Override
    @Transactional
    public Reserva cancelar(Long id) {
        ReservaEntity entity = buscarEntidad(id);
        Reserva reserva = entityMapper.toDomain(entity);
        reserva.cancelar();
        entity.setFechaHora(reserva.getFechaHora());
        reservaRepository.save(entity);
        log.info("Reserva id={} cancelada", id);
        return reserva;
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!reservaRepository.existsById(id)) {
            throw new ReservaNotFoundException("Reserva no encontrada con id: " + id);
        }
        reservaRepository.deleteById(id);
        log.info("Reserva id={} eliminada", id);
    }

    private ReservaEntity buscarEntidad(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNotFoundException("Reserva no encontrada con id: " + id));
    }
}
