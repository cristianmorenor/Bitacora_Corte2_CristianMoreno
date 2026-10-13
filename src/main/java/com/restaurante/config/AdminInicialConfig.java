package com.restaurante.config;

import com.restaurante.model.domain.Rol;
import com.restaurante.model.domain.Usuario;
import com.restaurante.repository.UsuarioRepository;
import com.restaurante.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Crea el primer ADMIN desde variables de entorno; las credenciales nunca van en el codigo
@Slf4j
@Configuration
public class AdminInicialConfig {

    @Bean
    public CommandLineRunner crearAdminInicial(UsuarioService usuarioService,
                                               UsuarioRepository usuarioRepository,
                                               @Value("${app.admin.username:}") String username,
                                               @Value("${app.admin.password:}") String password) {
        return args -> {
            if (username.isBlank() || password.isBlank()) {
                log.warn("ADMIN_USERNAME / ADMIN_PASSWORD no configurados: no se crea el administrador inicial");
                return;
            }
            if (usuarioRepository.existsByUsernameIgnoreCase(username)) {
                return;
            }
            usuarioService.crear(new Usuario(null, username, password, Rol.ADMIN, true));
            log.info("Administrador inicial '{}' creado", username);
        };
    }
}
