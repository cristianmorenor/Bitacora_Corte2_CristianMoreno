package com.restaurante.repository;

import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.persistence.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {

    List<PedidoEntity> findByEstado(EstadoPedido estado);

    List<PedidoEntity> findByMesaId(Long idMesa);
}