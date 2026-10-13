package com.restaurante.repository;

import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.persistence.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {

    List<PedidoEntity> findByEstado(EstadoPedido estado);

    List<PedidoEntity> findByMesaId(Long idMesa);

    // JOIN FETCH para evitar el problema 1+N al listar pedidos con sus items
    @Query("select distinct p from PedidoEntity p left join fetch p.items order by p.id")
    List<PedidoEntity> findAllConItems();
}
