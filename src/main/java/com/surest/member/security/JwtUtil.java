package com.surest.member.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "c2VjdXJlLXNlY3JldC1rZXktZm9yLUhTMjU2LWFsZ29yaXRobQ=="; // 256-bit Base64
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());  // secure key

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) &&
                !Jwts.parserBuilder().setSigningKey(key).build()
                        .parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }
}
