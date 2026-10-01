package com.restaurante.repository;

import com.restaurante.persistence.entity.RegistroVehiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroVehiculoRepository extends JpaRepository<RegistroVehiculoEntity, Long> {

    List<RegistroVehiculoEntity> findBySalidaIsNull();
}