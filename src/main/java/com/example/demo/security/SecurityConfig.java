package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_STATIC_RESOURCES = {
            "/",
            "/error",
            "/favicon.ico",
            "/css/**",
            "/js/**",
            "/html/**",
            "/poster/**",
            "/lor/**"
    };

    private static final String[] PUBLIC_READ_APIS = {
            "/api/phim/**",
            "/api/rap/**",
            "/api/suat-chieu/**",
            "/api/ghe/**",
            "/api/thanh-pho/**",
            "/api/the-loai/**",
            "/api/loai-ghe/**",
            "/api/loai-phong/**",
            "/api/phong-chieu/**",
            "/api/do-an/**"
    };

    private static final String[] ADMIN_MANAGED_APIS = {
            "/api/phim/**",
            "/api/rap/**",
            "/api/suat-chieu/**",
            "/api/ghe/**",
            "/api/thanh-pho/**",
            "/api/the-loai/**",
            "/api/loai-ghe/**",
            "/api/loai-phong/**",
            "/api/phong-chieu/**",
            "/api/do-an/**"
    };

    private final JwtAuthTokenFilter jwtAuthTokenFilter;

    public SecurityConfig(JwtAuthTokenFilter jwtAuthTokenFilter) {
        this.jwtAuthTokenFilter = jwtAuthTokenFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(PUBLIC_STATIC_RESOURCES).permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_READ_APIS).permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/hoa-don/**").authenticated()
                        .requestMatchers("/api/ve/**").authenticated()
                        .requestMatchers(HttpMethod.POST, ADMIN_MANAGED_APIS).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, ADMIN_MANAGED_APIS).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, ADMIN_MANAGED_APIS).hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
