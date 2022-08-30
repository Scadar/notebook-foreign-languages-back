package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.CustomUserDetails;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.Role;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_CLAIM = "authorities";
    private final String jwtSecret;
    private final long jwtExpirationInMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String jwtSecret,
            @Value("${app.jwt.expiration}") long jwtExpirationInMs
    ) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public String generateToken(CustomUserDetails customUserDetails) {
        Instant expiryDate = Instant.now().plusMillis(jwtExpirationInMs);
        String authorities = getUserAuthorities(customUserDetails);
        return Jwts.builder()
                   .setSubject(Long.toString(customUserDetails.getId()))
                   .setIssuedAt(Date.from(Instant.now()))
                   .setExpiration(Date.from(expiryDate))
                   .signWith(SignatureAlgorithm.HS512, jwtSecret)
                   .claim(AUTHORITIES_CLAIM, authorities)
                   .claim("id", customUserDetails.getId())
                   .claim("createdAt", customUserDetails.getCreatedAt())
                   .claim("isActive", customUserDetails.isEnabled())
                   .claim("email", customUserDetails.getEmail())
                   .claim("firstName", customUserDetails.getFirstName())
                   .claim("lastName", customUserDetails.getLastName())
                   .claim("username", customUserDetails.getUsername())
                   .claim("emailIsVerified", customUserDetails.getIsEmailVerified())
                   .claim(
                           "roles",
                           customUserDetails.getRoles().stream().map(Role::getRole).collect(Collectors.toList())
                   )
                   .compact();
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                            .setSigningKey(jwtSecret)
                            .parseClaimsJws(token)
                            .getBody();

        return Long.parseLong(claims.getSubject());
    }


    public long getExpiryDuration() {
        return jwtExpirationInMs;
    }

    public List<GrantedAuthority> getAuthoritiesFromJWT(String token) {
        Claims claims = Jwts.parser()
                            .setSigningKey(jwtSecret)
                            .parseClaimsJws(token)
                            .getBody();
        return Arrays.stream(claims.get(AUTHORITIES_CLAIM).toString().split(","))
                     .map(SimpleGrantedAuthority::new)
                     .collect(Collectors.toList());
    }

    private String getUserAuthorities(CustomUserDetails customUserDetails) {
        return customUserDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

}
