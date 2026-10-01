package com.restaurante.repository;

import com.restaurante.persistence.entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {

    List<ReservaEntity> findByMesaId(Long idMesa);
}