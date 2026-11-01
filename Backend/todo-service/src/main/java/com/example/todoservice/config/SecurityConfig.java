package com.example.todoservice.config;
import com.example.todoservice.security.JwtAuthenticationFilter;
import com.example.todoservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//protect the todo apis and control the access to them
//HTTP Method	    Endpoint	Allowed Roles
// POST	            /todos/**	USER, ADMIN
// GET	            /todos/**	USER, ADMIN
// PUT	            /todos/**	USER, ADMIN
// PATCH	        /todos/**	USER, ADMIN
// DELETE	        /todos/**	USER, ADMIN


@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Public health endpoints
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/health/**"
                        ).permitAll()

                        // Allow preflight CORS requests
                        .requestMatchers(
                                HttpMethod.OPTIONS, "/**"
                        ).permitAll()

                        // USER and ADMIN can create todos
                        .requestMatchers(HttpMethod.POST, "/todos/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // USER and ADMIN can view todos
                        .requestMatchers(HttpMethod.GET, "/todos/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // USER and ADMIN can update todos
                        .requestMatchers(HttpMethod.PUT, "/todos/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // USER and ADMIN can partially update todos
                        .requestMatchers(HttpMethod.PATCH, "/todos/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // USER and ADMIN can delete todos
                        .requestMatchers(HttpMethod.DELETE, "/todos/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // Deny all other requests
                        .anyRequest().denyAll()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}