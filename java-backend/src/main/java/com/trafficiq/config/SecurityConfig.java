package com.trafficiq.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Allows H2 console
            .authorizeHttpRequests(auth -> auth
                // Public authentication and documentation
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()

                // Public read-only browsing & monitoring
                .requestMatchers(HttpMethod.GET, "/api/locations/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/roads/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/incidents/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/traffic/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/analytics/**").permitAll()

                // Public route planning preview
                .requestMatchers(HttpMethod.POST, "/api/routes/plan").permitAll()

                // Admin-restricted mutation operations
                .requestMatchers(HttpMethod.POST, "/api/roads/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/roads/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/roads/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/incidents/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/incidents/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/incidents/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/traffic/**").hasRole("ADMIN")

                // Any other request authenticated
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*")); // Allow any frontend origin
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
