package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class CorsConfig {

    @Bean
    public SecurityFilterChain filterChain2(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // cần cho test POST/PUT/DELETE nhanh
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/test/**",
                                "/api/test/**",
                                "/api/auth/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
