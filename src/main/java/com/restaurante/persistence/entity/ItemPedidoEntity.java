package com.restaurante.persistence.entity;

import com.restaurante.model.domain.TerminoCoccion;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items_pedido")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ItemPedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pedido", nullable = false)
    private PedidoEntity pedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_plato", nullable = false)
    private PlatoEntity plato;

    @Column(name = "nombre_plato", nullable = false, length = 100)
    private String nombrePlato;

    @Column(name = "precio_congelado", nullable = false)
    private Double precioCongelado;

    @Column(nullable = false)
    private Integer cantidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "termino_coccion", length = 20)
    private TerminoCoccion terminoCoccion;

    @Column(length = 255)
    private String observaciones;
}