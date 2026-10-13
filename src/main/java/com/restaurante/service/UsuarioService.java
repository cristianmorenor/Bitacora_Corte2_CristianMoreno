package com.restaurante.service;

import com.restaurante.model.domain.Usuario;

import java.util.List;

public interface UsuarioService {

    Usuario crear(Usuario usuario);

    Usuario obtenerPorUsername(String username);

    List<Usuario> obtenerTodos();
}
