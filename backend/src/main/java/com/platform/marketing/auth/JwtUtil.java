package com.platform.marketing.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private final String secret = "secret-key";
    private final long expireMs = 24 * 60 * 60 * 1000L;

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

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

    /**
     * Build an {@link UsernamePasswordAuthenticationToken} from the given JWT token.
     */
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        String username = getUsername(token);
        List<String> perms = getAuthorities(token);
        log.debug("JWT permissions: {}", perms);
        List<GrantedAuthority> authorities = perms.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
}
