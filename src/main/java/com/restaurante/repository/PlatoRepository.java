package com.restaurante.repository;

import com.restaurante.persistence.entity.PlatoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlatoRepository extends JpaRepository<PlatoEntity, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    List<PlatoEntity> findByActivoTrue();
}