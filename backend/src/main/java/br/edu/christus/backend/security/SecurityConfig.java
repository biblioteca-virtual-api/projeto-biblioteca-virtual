
package br.edu.christus.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize

                        // Autenticação e cadastro público
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()

                        // Swagger
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Usuários: somente administrador
                        .requestMatchers(
                                "/api/v1/users",
                                "/api/v1/users/**"
                        ).hasRole("ADMINISTRADOR")

                        // Livros: somente administrador
                        .requestMatchers(
                                "/api/v1/livros",
                                "/api/v1/livros/**"
                        ).hasRole("ADMINISTRADOR")

                        // Autores: qualquer usuário autenticado pode consultar
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/autores",
                                "/api/v1/autores/**"
                        ).authenticated()

                        // Demais operações de autores: administrador
                        .requestMatchers(
                                "/api/v1/autores",
                                "/api/v1/autores/**"
                        ).hasRole("ADMINISTRADOR")

                        // Gêneros: qualquer usuário autenticado pode consultar
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/generos",
                                "/api/v1/generos/**"
                        ).authenticated()

                        // Demais operações de gêneros: administrador
                        .requestMatchers(
                                "/api/v1/generos",
                                "/api/v1/generos/**"
                        ).hasRole("ADMINISTRADOR")

                        // Uploads: operações do administrador
                        .requestMatchers(
                                "/api/v1/uploads/pendentes",
                                "/api/v1/uploads/*/aprovar",
                                "/api/v1/uploads/*/rejeitar"
                        ).hasRole("ADMINISTRADOR")

                        // Uploads: operações do professor
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/uploads"
                        ).hasRole("PROFESSOR")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/uploads/meus"
                        ).hasRole("PROFESSOR")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/uploads/*"
                        ).hasRole("PROFESSOR")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/uploads/*/reenviar"
                        ).hasRole("PROFESSOR")

                        // Demais endpoints exigem autenticação
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        securityFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
