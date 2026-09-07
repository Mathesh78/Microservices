package com.example.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http)
//            throws Exception {
//
//        http
//                .csrf(csrf -> csrf.disable())
//
//                .authorizeHttpRequests(auth -> auth
//
//                        // Auth service does NOT require JWT
//                        .requestMatchers("/auth-service/**").permitAll()
//
//                        // Everything else requires JWT
//                        .anyRequest().authenticated()
//                )
//
//                .oauth2ResourceServer(oauth ->
//                        oauth.jwt(Customizer.withDefaults())
//                );
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // Register and login do not need JWT
                        .requestMatchers("/auth-service/**").permitAll()

                        // Question and Quiz APIs need JWT
                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth ->
                        oauth.jwt(Customizer.withDefaults())
                );

        return http.build();
    }
    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${jwt.secret}") String secret) {

        byte[] keyBytes =
                secret.getBytes(StandardCharsets.UTF_8);

        SecretKey key =
                new SecretKeySpec(
                        keyBytes,
                        "HmacSHA256"
                );

        return NimbusJwtDecoder
                .withSecretKey(key)
                .build();
    }
}