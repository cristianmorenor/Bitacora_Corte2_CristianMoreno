package com.restaurante.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private Long id;
    private String username;
    private String password;
    private Rol rol;
    private Boolean activo;

    public Boolean estaActivo() {
        return Boolean.TRUE.equals(activo);
    }
}
