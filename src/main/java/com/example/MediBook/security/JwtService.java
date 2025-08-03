package com.example.MediBook.security;

import com.example.MediBook.configuration.CustomUserDetails;
import com.example.MediBook.entity.Permission;
import com.example.MediBook.entity.Role;
import com.example.MediBook.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(CustomUserDetails userDetails) {
        User user=userDetails.getUser();
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id",user.getId())
                .claim("name",user.getName())
                .claim("roles",user.getRoles().stream()
                        .map(Role::getName).toList())
                .claim("permissions",user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(Permission::getName)
                        .distinct().toList())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+jwtExpiration))
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public LocalDateTime extractExpiration(String token){
        Date exp=Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getExpiration();
        return exp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
