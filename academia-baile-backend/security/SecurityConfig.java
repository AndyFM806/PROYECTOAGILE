package com.academiabaile.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // desactiva CSRF para permitir peticiones POST desde frontend
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/**", "/uploads/**", "/static/**").permitAll() // acceso libre
                                .anyRequest().authenticated() // lo demás requiere login (por si agregas futuro admin)
                )
                .formLogin(login -> login.disable()) // desactiva formulario login
                .httpBasic(basic -> basic.disable()); // desactiva login básico

        return http.build();
    }
}
