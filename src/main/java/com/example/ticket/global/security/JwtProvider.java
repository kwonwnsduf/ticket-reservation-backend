package com.example.ticket.global.security;

import com.example.ticket.domain.member.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
    private final Key key;
    private final long expireMs;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expireMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireMs = expireMs;
    }
    public String createToken(Long memberId, Role role){
        Claims claims= Jwts.claims();
        claims.put("memberId",memberId);
        claims.put("role",role.name());

        return Jwts.builder().setClaims(claims).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis()+expireMs)).signWith(SignatureAlgorithm.HS256,key).compact();
    }
    public Claims parse(String token){
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }
}
