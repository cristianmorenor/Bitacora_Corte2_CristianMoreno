package com.restaurante.service;

import com.restaurante.exception.CredencialesInvalidasException;
import com.restaurante.model.domain.Rol;
import com.restaurante.model.domain.Usuario;
import com.restaurante.model.dto.request.LoginRequestDTO;
import com.restaurante.model.dto.request.RegistroClienteRequestDTO;
import com.restaurante.model.dto.response.AuthResponseDTO;
import com.restaurante.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    @Override
    public AuthResponseDTO login(LoginRequestDTO dto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.username(), dto.password()));
        } catch (AuthenticationException ex) {
            log.warn("Intento de login fallido para '{}'", dto.username());
            throw new CredencialesInvalidasException("Usuario o contrasena incorrectos");
        }

        Usuario usuario = usuarioService.obtenerPorUsername(dto.username());
        log.info("Usuario '{}' inicio sesion", usuario.getUsername());
        return construirRespuesta(usuario);
    }

    @Override
    public AuthResponseDTO registrarCliente(RegistroClienteRequestDTO dto) {
        Usuario usuario = new Usuario(null, dto.username(), dto.password(), Rol.CLIENTE, true);
        return construirRespuesta(usuarioService.crear(usuario));
    }

    private AuthResponseDTO construirRespuesta(Usuario usuario) {
        String token = jwtService.generarToken(usuario.getUsername(), usuario.getRol());
        return new AuthResponseDTO(token, "Bearer", usuario.getUsername(),
                usuario.getRol().name(), jwtService.getExpiracionMinutos());
    }
}
