package com.restaurante.security;

import com.restaurante.model.domain.Rol;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private static final String CLAIM_ROL = "rol";

    private final SecretKey llave;

    @Getter
    private final long expiracionMinutos;

    public JwtService(@Value("${app.jwt.secret}") String secreto,
                      @Value("${app.jwt.expiration-minutes}") long expiracionMinutos) {
        byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos 32 caracteres");
        }
        this.llave = Keys.hmacShaKeyFor(bytes);
        this.expiracionMinutos = expiracionMinutos;
    }

    public String generarToken(String username, Rol rol) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim(CLAIM_ROL, rol.name())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plus(expiracionMinutos, ChronoUnit.MINUTES)))
                .signWith(llave)
                .compact();
    }

    // Lanza JwtException si la firma no coincide o el token ya vencio
    public Claims validarYObtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(llave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean esValido(String token) {
        try {
            validarYObtenerClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String extraerUsername(String token) {
        return validarYObtenerClaims(token).getSubject();
    }

    public Rol extraerRol(String token) {
        return Rol.valueOf(validarYObtenerClaims(token).get(CLAIM_ROL, String.class));
    }
}
