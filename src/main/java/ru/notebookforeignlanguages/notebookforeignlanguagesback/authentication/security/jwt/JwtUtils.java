package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.security.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.security.service.UserDetailsImpl;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.exception.CustomException;

import java.util.Date;

@Component
public class JwtUtils {
    @Value("${app.security.jwt-secret}")
    private String jwtSecret;

    @Value("${app.security.jwt-expiration-ms}")
    private int jwtExpirationMs;

    public String generateJwtToken(UserDetailsImpl userPrincipal) {
        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    public String generateTokenFromUsername(String username) {
        return Jwts
                .builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(
                        SignatureAlgorithm.HS512,
                        jwtSecret
                )
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            throw new CustomException("Invalid JWT signature", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MalformedJwtException e) {
            throw new CustomException("Invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ExpiredJwtException e) {
            throw new CustomException("JWT token is expired", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (UnsupportedJwtException e) {
            throw new CustomException("JWT token is unsupported", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            throw new CustomException("JWT claims string is empty", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
