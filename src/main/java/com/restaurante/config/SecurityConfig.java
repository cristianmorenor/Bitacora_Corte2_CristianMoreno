package com.restaurante.config;

import com.restaurante.security.JwtAuthenticationFilter;
import com.restaurante.security.RestSecurityErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String MESERO = "MESERO";
    private static final String COCINERO = "COCINERO";
    private static final String CAJERO = "CAJERO";
    private static final String CLIENTE = "CLIENTE";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestSecurityErrorHandler securityErrorHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // API stateless con JWT: no hay cookie de sesion, por eso CSRF no aplica
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(securityErrorHandler)
                        .accessDeniedHandler(securityErrorHandler))
                .authorizeHttpRequests(auth -> auth
                        // Publico
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/menu/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()

                        // Usuarios del personal
                        .requestMatchers("/api/v1/usuarios/**").hasRole(ADMIN)

                        // Carta (administracion de platos)
                        .requestMatchers(HttpMethod.GET, "/api/v1/platos/**").hasAnyRole(ADMIN, MESERO, COCINERO)
                        .requestMatchers("/api/v1/platos/**").hasRole(ADMIN)

                        // Cuentas y pagos
                        .requestMatchers(HttpMethod.POST, "/api/v1/cuentas/*/pagos").hasAnyRole(ADMIN, CAJERO)
                        .requestMatchers("/api/v1/mesas/*/cuentas", "/api/v1/cuentas/**").hasAnyRole(ADMIN, MESERO, CAJERO)

                        // Mesas
                        .requestMatchers(HttpMethod.POST, "/api/v1/mesas").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/mesas/**").hasAnyRole(ADMIN, MESERO)
                        .requestMatchers(HttpMethod.GET, "/api/v1/mesas/**").hasAnyRole(ADMIN, MESERO, CAJERO)

                        // Pedidos
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/pedidos/*/estado").hasAnyRole(ADMIN, COCINERO, MESERO)
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/*/historial").hasAnyRole(ADMIN, COCINERO, MESERO)
                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos", "/api/v1/pedidos/*/items").hasAnyRole(ADMIN, MESERO, CLIENTE)
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos").hasAnyRole(ADMIN, MESERO, COCINERO, CAJERO)
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/*").hasAnyRole(ADMIN, MESERO, COCINERO, CAJERO, CLIENTE)

                        // Reservas
                        .requestMatchers(HttpMethod.POST, "/api/v1/reservas").hasAnyRole(ADMIN, MESERO, CLIENTE)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/reservas/**").hasRole(ADMIN)
                        .requestMatchers("/api/v1/reservas/**").hasAnyRole(ADMIN, MESERO)

                        // Parqueadero
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/parqueadero/**").hasRole(ADMIN)
                        .requestMatchers("/api/v1/parqueadero/**").hasAnyRole(ADMIN, CAJERO)

                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
