package com.fidelity.moneytransfer.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtUtil {

    // Ideally, store it in an env variable or a configuration file
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);  // Generates a new secret key for HS256 algorithm

    // Generate token
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(secretKey)  // Signing with the updated way
                .compact();
    }

    // Extract username (email) from token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Extract all claims
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()  // New method to create a parser
                .setSigningKey(secretKey)  // Use the secret key to parse and validate the token
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Validate the token
    public boolean isTokenValid(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
