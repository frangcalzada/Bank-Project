package com.bankproject.bankproject_core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableMethodSecurity   // Habilita @PreAuthorize y seguridad a nivel método
public class SecurityConfig {

    /**
     * Reglas de autorización a nivel de request:
     * - /actuator/health: libre (para readiness/liveness)
     * - /api/admin/**: requiere rol ADMIN o scope admin (ejemplo)
     * - demás: autenticados (Bearer JWT válido)
     * Además:
     * - Activamos CORS (con CorsConfigurationSource)
     * - Activamos Resource Server (JWT) → valida automáticamente por issuer-uri
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1) CORS primero (para que el preflight pase correctamente)
                .cors(Customizer.withDefaults())

                // 2) CSRF no aplica para una API stateless con Bearer tokens
                .csrf(csrf -> csrf.disable())

                // 3) Autorización por rutas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/api/admin/**").hasAnyAuthority("SCOPE_admin","ROLE_ADMIN")
                        .anyRequest().authenticated()
                )

                // 4) Configuramos el servidor de recursos con JWT
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(Customizer.withDefaults())
                );

        return http.build();
    }

    /**
     * CORS de desarrollo:
     * Permitimos el origen del Frontend (Vite en 5173).
     * Ajusta métodos/headers según necesites; aquí GET/POST/PUT/DELETE + Authorization.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of("http://localhost:5173"));
        cors.setAllowedMethods(List.of(HttpMethod.GET.name(), HttpMethod.POST.name(),
                HttpMethod.PUT.name(), HttpMethod.DELETE.name()));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        cors.setAllowCredentials(false); // para SPA con Bearer, típicamente false

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }
}