package com.restaurante.security;

import com.restaurante.model.domain.Rol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService("clave-solo-para-tests-con-mas-de-32-caracteres", 60);

    @Test
    @DisplayName("Generar token - debe llevar el usuario y el rol")
    void generarToken_debeIncluirUsuarioYRol() {
        String token = jwtService.generarToken("cocina1", Rol.COCINERO);

        assertTrue(jwtService.esValido(token));
        assertEquals("cocina1", jwtService.extraerUsername(token));
        assertEquals(Rol.COCINERO, jwtService.extraerRol(token));
    }

    @Test
    @DisplayName("Token firmado con otra llave - no debe ser valido")
    void esValido_otraLlave_debeSerFalso() {
        JwtService otro = new JwtService("otra-clave-distinta-con-mas-de-32-caracteres", 60);
        String token = otro.generarToken("admin", Rol.ADMIN);

        assertFalse(jwtService.esValido(token));
    }

    @Test
    @DisplayName("Token vencido - no debe ser valido")
    void esValido_tokenVencido_debeSerFalso() {
        JwtService expirado = new JwtService("clave-solo-para-tests-con-mas-de-32-caracteres", -1);
        String token = expirado.generarToken("admin", Rol.ADMIN);

        assertFalse(jwtService.esValido(token));
    }

    @Test
    @DisplayName("Secreto muy corto - debe rechazarse al iniciar")
    void constructor_secretoCorto_debeLanzarExcepcion() {
        assertThrows(IllegalStateException.class, () -> new JwtService("corta", 60));
    }
}
