package com.eduplatform.apiUsuario.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;


import java.util.*;
import java.security.Key;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expiration = 1000 * 60 * 60 * 10;

    public String generarToker(String email, Collection<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
}

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
            }
    }

    public String extraerEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public List<String> extraerRoles(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();

        Object roles = claims.get("roles");
        if (roles instanceof List<?> lista) {
            return lista.stream().map(Object::toString).collect(Collectors.toList());
        }
        return List.of();
    }

}
