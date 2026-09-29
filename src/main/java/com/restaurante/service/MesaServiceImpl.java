package com.restaurante.service;

import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class MesaServiceImpl implements MesaService {

    private final Map<Long, Mesa> mesas = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(1);

    @Override
    public Mesa crear(Mesa mesa) {
        mesa.setId(secuencia.getAndIncrement());
        mesas.put(mesa.getId(), mesa);
        log.info("Mesa numero {} creada con id={}", mesa.getNumero(), mesa.getId());
        return mesa;
    }

    @Override
    public Mesa obtenerPorId(Long id) {
        return mesas.values().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new MesaNotFoundException("Mesa no encontrada con id: " + id));
    }

    @Override
    public List<Mesa> obtenerTodas() {
        return mesas.values().stream().toList();
    }

    @Override
    public void ocupar(Long id) {
        Mesa mesa = obtenerPorId(id);
        mesa.ocupar();
        log.info("Mesa id={} marcada como OCUPADA", id);
    }

    @Override
    public void liberar(Long id) {
        Mesa mesa = obtenerPorId(id);
        mesa.liberar();
        log.info("Mesa id={} marcada como DISPONIBLE", id);
    }
}