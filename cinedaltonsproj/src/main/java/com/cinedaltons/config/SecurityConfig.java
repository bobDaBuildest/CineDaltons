package com.cinedaltons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // disable CSRF for development
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index", "/index.html", "/css/**", "/js/**", "/images/**",
                                "/api/auth/signup").permitAll() // allow signup & index.html
                )
                .formLogin(form -> form.disable()); // disable Spring login form completely

        return http.build();
    }
}
