package com.esporteclubepelotas.api.common.security;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          RestAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        // 1. Health Checks, Raiz & Documentacao Swagger OpenAPI
                        .requestMatchers("/", "/health", "/actuator/**", "/ws/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // 2. Autenticacao Publica & Registro
                        .requestMatchers(
                                "/auth/login",
                                "/auth/register",
                                "/auth/refresh",
                                "/auth/verify",
                                "/auth/resend-code",
                                "/auth/forgot-password",
                                "/auth/reset-password",
                                "/auth/env-test",
                                "/users/register",
                                "/users/check-availability"
                        ).permitAll()

                        // 3. Rotas Administrativas restritas
                        .requestMatchers("/timeline/admin/**").authenticated()
                        .requestMatchers("/athletes/admin/**").authenticated()
                        .requestMatchers("/squads/admin/**").authenticated()
                        .requestMatchers("/idols/admin/**").authenticated()
                        .requestMatchers("/news/admin/**").authenticated()
                        .requestMatchers("/users/admin/**").authenticated()
                        .requestMatchers("/kits/admin/**").authenticated()

                        // 4. Consultas Publicas (GET)
                        .requestMatchers(HttpMethod.GET, "/idols", "/idols/*", "/idols/*/prev-next", "/idols/featured").permitAll()
                        .requestMatchers(HttpMethod.GET, "/news/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/squads/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/athletes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/timeline", "/timeline/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/settings", "/settings/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/themes/active", "/themes/active/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/stadium", "/stadium/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/sponsors", "/sponsors/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/sponsors/*/click").permitAll()
                        .requestMatchers(HttpMethod.GET, "/anthem", "/anthem/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/symbols", "/symbols/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/kits/public", "/kits/public/**").permitAll()

                        // 5. Todas as rotas administrativas e modificacoes exigem autenticacao
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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
