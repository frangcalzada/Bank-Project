package com.bankproject.bankproject.auth.config;


import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.*;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class AuthServerConfig {

    /* Cadena de filtros #1: Endpoints del Authorization Server (OIDC/OAuth2) */

    @Bean
    @Order(1)
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        var authorizationServerConfigurer =
                org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
                        .authorizationServer();

        http
                // Aplica el configurer del Authorization Server a sus endpoints
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, authz ->
                        authz.oidc(Customizer.withDefaults())  // habilita OIDC (discovery, userinfo, etc.)
                );

        // Login de formulario simple para probar
        return http.formLogin(Customizer.withDefaults()).build();
    }

        /* Cadena de filtros #2: Seguridad "general" de la app (cualquier otra ruta) */
    @Bean
    @Order(2)
    public SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .formLogin(Customizer.withDefaults());
        return http.build();
    }

    /* Usuarios de prueba (InMemory) con roles de negocio
       - En producción vendrían de tu base de usuarios; aquí es suficiente para flows de OAuth2. */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails teller = User.withUsername("teller")
                .password(encoder.encode("teller123"))
                .roles("TELLER")
                .build();

        UserDetails customer = User.withUsername("customer")
                .password(encoder.encode("customer123"))
                .roles("CUSTOMER")
                .build();

        return new InMemoryUserDetailsManager(admin, teller, customer);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* Cliente "public SPA" (React) con Authorization Code + PKCE y Refresh Token */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient spaClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("bank-web")                                     // identificador del SPA
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE) // SPA = cliente público (sin secret)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) // flujo recomendado para SPA
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:5173/auth/callback")         // callback del front en dev
                .postLogoutRedirectUri("http://localhost:5173/")
                .scope(OidcScopes.OPENID)                                   // OIDC (id_token)
                .scope("accounts.read")                                     // scopes de negocio
                .scope("transfers.write")
                .scope("admin")                                             // reservado a ADMIN
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(true)                              // PKCE obligatorio para SPA
                        .requireAuthorizationConsent(true)                  // pantalla de consentimiento
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(java.time.Duration.ofMinutes(15))
                        .refreshTokenTimeToLive(java.time.Duration.ofDays(7))
                        .reuseRefreshTokens(false)                          // rotación de refresh tokens
                        .build())
                .build();

        return new InMemoryRegisteredClientRepository(spaClient);
    }

    /* Par de claves RSA (dev) publicado como JWKS -> firma de JWT.
       En prod, la clave se almacena en AWS Secrets Manager/HSM, no en memoria. */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsa = generateRsa();
        JWKSet jwkSet = new JWKSet(rsa);
        return (selector, context) -> selector.select(jwkSet);
    }

    private static RSAKey generateRsa() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair keyPair = kpg.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            return new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(UUID.randomUUID().toString())
                    .build();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /* Issuer del servidor de autorización (coincide con server.port) */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:9000")
                .build();
    }
}