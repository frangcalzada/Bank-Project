package com.bankproject.bankproject_core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity // Habilita @PreAuthorize en métodos/controladores
public class SecurityConfig {

    /**
     * Reglas de autorización:
     * - /actuator/health: libre
     * - /api/admin/**: exige SCOPE_admin (por ahora trabajamos solo con scopes)
     * - resto: autenticado (Bearer JWT)
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())              // 1) CORS primero
                .csrf(csrf -> csrf.disable())                 // 2) CSRF off (API stateless)
                .authorizeHttpRequests(auth -> auth           // 3) Autorización por rutas
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/api/admin/**").hasAuthority("SCOPE_admin")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults())); // 4) JWT Resource Server

        return http.build();
    }

    /**
     * CORS parametrizable via app.cors.allowed-origins (CSV).
     * Ej: "http://localhost:5173"
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") String originsCsv) {

        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(Arrays.asList(originsCsv.split(",")));
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        cors.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }
}