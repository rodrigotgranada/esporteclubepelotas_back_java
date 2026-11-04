package com.esporteclubepelotas.api.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(401);
                            response.getWriter().write("{\"statusCode\":401,\"message\":\"Não autenticado. Forneça um token JWT válido.\",\"error\":\"Unauthorized\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // 1. Health Checks, Raiz & Documentacao Swagger OpenAPI
                        .requestMatchers("/", "/health", "/actuator/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // 2. Autenticacao Publica
                        .requestMatchers("/auth/login", "/auth/register", "/auth/refresh").permitAll()

                        // 3. Consultas Publicas (GET)
                        .requestMatchers(HttpMethod.GET, "/news/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/idols/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/squads/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/athletes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/settings", "/settings/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/themes/active", "/themes/active/**").permitAll()

                        // 4. Todas as rotas administrativas e modificacoes exigem autenticacao
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
