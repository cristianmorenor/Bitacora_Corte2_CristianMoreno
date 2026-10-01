package com.restaurante.repository;

import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.persistence.entity.MesaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MesaRepository extends JpaRepository<MesaEntity, Long> {

    boolean existsByNumero(Integer numero);

    List<MesaEntity> findByEstado(EstadoMesa estado);
}