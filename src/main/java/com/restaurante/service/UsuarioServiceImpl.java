package com.restaurante.service;

import com.restaurante.exception.UsuarioAlreadyExistsException;
import com.restaurante.mapper.UsuarioPersistenceMapper;
import com.restaurante.model.domain.Usuario;
import com.restaurante.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioPersistenceMapper entityMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Usuario crear(Usuario usuario) {
        if (usuarioRepository.existsByUsernameIgnoreCase(usuario.getUsername())) {
            throw new UsuarioAlreadyExistsException("Ya existe un usuario con nombre: " + usuario.getUsername());
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setActivo(true);

        Usuario creado = entityMapper.toDomain(usuarioRepository.save(entityMapper.toEntity(usuario)));
        log.info("Usuario '{}' creado con rol {}", creado.getUsername(), creado.getRol());
        return creado;
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorUsername(String username) {
        return usuarioRepository.findByUsernameIgnoreCase(username)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
    }
}
