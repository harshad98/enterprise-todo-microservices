package org.example.authservice.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

//contains rules about
//which api will be publicly accessible, which is protected
//means rulebook for who can access which api
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Registration is public
                        .requestMatchers(
                                HttpMethod.POST,
                                "/auth/register"
                        ).permitAll()

                        // Login is public
                        .requestMatchers(
                                HttpMethod.POST,
                                "/auth/login"
                        ).permitAll()

                        // Admin registration is public - TEMPORARY for testing
                        .requestMatchers(
                                HttpMethod.POST,
                                "/auth/register-admin"
                        ).permitAll()

                        // Health check is public
                        .requestMatchers(
                                "/actuator/health/**"
                        ).permitAll()

                        // Deny all other endpoints for now
                        .anyRequest().denyAll()
                );

        return http.build();
    }
}
