package com.restaurante.service;

import com.restaurante.exception.PlatoAlreadyExistsException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.model.domain.Plato;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class PlatoServiceImpl implements PlatoService {

    private final Map<Long, Plato> platos = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(1);

    @Override
    public Plato crear(Plato plato) {
        boolean nombreDuplicado = platos.values().stream()
                .anyMatch(p -> p.getNombre().equalsIgnoreCase(plato.getNombre()));

        if (nombreDuplicado) {
            log.warn("Intento de crear plato duplicado: '{}'", plato.getNombre());
            throw new PlatoAlreadyExistsException("Ya existe un plato con nombre: " + plato.getNombre());
        }

        plato.setId(secuencia.getAndIncrement());
        platos.put(plato.getId(), plato);
        log.info("Plato '{}' creado con id={}", plato.getNombre(), plato.getId());
        return plato;
    }

    @Override
    public Plato obtenerPorId(Long id) {
        return platos.values().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new PlatoNotFoundException("Plato no encontrado con id: " + id));
    }

    @Override
    public List<Plato> obtenerTodos() {
        return platos.values().stream().toList();
    }

    @Override
    public List<Plato> obtenerDisponibles() {
        return platos.values().stream()
                .filter(Plato::getDisponible)
                .toList();
    }

    @Override
    public Plato actualizar(Long id, Plato platoActualizado) {
        Plato existente = obtenerPorId(id);
        existente.setNombre(platoActualizado.getNombre());
        existente.setPrecio(platoActualizado.getPrecio());
        existente.setCategoria(platoActualizado.getCategoria());
        log.info("Plato con id={} actualizado", id);
        return existente;
    }

    @Override
    public void eliminar(Long id) {
        Plato plato = obtenerPorId(id);
        platos.remove(plato.getId());
        log.info("Plato con id={} eliminado", id);
    }
}