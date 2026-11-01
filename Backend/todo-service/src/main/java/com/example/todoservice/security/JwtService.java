package com.example.todoservice.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//read and validate JWT tokens, extract the user's ID and roles,
//and help Spring Security determine whether the request is authorized.
@Service
public class JwtService {

    private final SecretKey key;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public Long extractUserId(String token) {
        String subject = getClaims(token).getSubject();
        return Long.parseLong(subject);
    }

    public List<String> extractRoles(String token) {
        Object rolesClaim = getClaims(token).get("roles");

        if (!(rolesClaim instanceof List<?> rolesList)) {
            throw new IllegalArgumentException("Invalid roles claim");
        }

        List<String> roles = new ArrayList<>();

        for (Object role : rolesList) {
            if (!(role instanceof String roleName)
                    || roleName.isBlank()) {
                throw new IllegalArgumentException(
                        "Invalid role in JWT"
                );
            }

            roles.add(roleName);
        }

        return roles;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}