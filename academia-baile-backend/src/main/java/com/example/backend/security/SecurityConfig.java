package com.example.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/clases",
                    "/api/clases/**",
                    "/api/inscripciones",
                    "/api/inscripciones/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(); // puedes usar .formLogin() si tienes login por formulario
        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                            "https://marvelous-snickerdoodle-04c3ef.netlify.app",
                            "http://localhost:5500",
                            "http://127.0.0.1:5500"
                        )
                        .allowedMethods("*");
            }
        };
    }
}
