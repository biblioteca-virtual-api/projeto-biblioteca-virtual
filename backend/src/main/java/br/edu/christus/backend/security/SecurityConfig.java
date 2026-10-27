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
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        // Cadastro público: sem token de ADMINISTRADOR, o UsuarioService só cria ALUNO (RF03).
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // O resto de /users (listar, excluir...) é só do administrador: a listagem mostra o e-mail
                        // de todos. Precisa ficar depois do POST público. Sem HttpMethod de propósito: assim
                        // nenhum método (GET, HEAD, DELETE...) escapa da regra.
                        .requestMatchers("/api/v1/users", "/api/v1/users/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/v1/livros", "/api/v1/livros/**").hasRole("ADMINISTRADOR")
                        // Análise de uploads (RF36 a RF39): só o administrador vê a fila e aprova/rejeita.
                        // Sem HttpMethod de propósito: com GET, um HEAD escaparia da regra e chegaria ao controller.
                        .requestMatchers("/api/v1/uploads/pendentes", "/api/v1/uploads/*/aprovar",
                                "/api/v1/uploads/*/rejeitar").hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}