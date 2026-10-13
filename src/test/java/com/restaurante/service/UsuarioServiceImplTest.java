package com.restaurante.service;

import com.restaurante.exception.UsuarioAlreadyExistsException;
import com.restaurante.mapper.UsuarioPersistenceMapper;
import com.restaurante.mapper.UsuarioPersistenceMapperImpl;
import com.restaurante.model.domain.Rol;
import com.restaurante.model.domain.Usuario;
import com.restaurante.persistence.entity.UsuarioEntity;
import com.restaurante.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private final UsuarioPersistenceMapper entityMapper = new UsuarioPersistenceMapperImpl();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UsuarioServiceImpl usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioServiceImpl(usuarioRepository, entityMapper, passwordEncoder);
    }

    @Test
    @DisplayName("Crear usuario - debe guardar la contrasena cifrada con BCrypt")
    void crear_usuarioNuevo_debeCifrarContrasena() {
        when(usuarioRepository.existsByUsernameIgnoreCase("mesero1")).thenReturn(false);
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario creado = usuarioService.crear(new Usuario(null, "mesero1", "Clave12345", Rol.MESERO, null));

        assertNotEquals("Clave12345", creado.getPassword());
        assertTrue(passwordEncoder.matches("Clave12345", creado.getPassword()));
        assertTrue(creado.estaActivo());
    }

    @Test
    @DisplayName("Crear usuario con nombre repetido - debe lanzar excepcion")
    void crear_usuarioRepetido_debeLanzarExcepcion() {
        when(usuarioRepository.existsByUsernameIgnoreCase("mesero1")).thenReturn(true);

        assertThrows(UsuarioAlreadyExistsException.class,
                () -> usuarioService.crear(new Usuario(null, "mesero1", "Clave12345", Rol.MESERO, true)));
        verify(usuarioRepository, never()).save(any());
    }
}
