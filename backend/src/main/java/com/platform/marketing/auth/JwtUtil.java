package com.platform.marketing.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private final String secret = "secret-key";
    private final long expireMs = 24 * 60 * 60 * 1000L;

    public String generateToken(org.springframework.security.core.userdetails.UserDetails user) {
        Date now = new Date();
        List<String> auths = user.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("auth", auths)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireMs))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public List<String> getAuthorities(String token) {
        Object obj = getClaims(token).get("auth");
        if (obj instanceof List<?>) {
            return ((List<?>) obj).stream().map(String::valueOf).collect(Collectors.toList());
        }
        return java.util.Collections.emptyList();
    }

    public boolean validate(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
}
