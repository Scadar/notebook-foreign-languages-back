package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.models.RefreshToken;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.repository.AppUserRepository;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.repository.RefreshTokenRepository;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${app.security.jwt-refresh-expiration-ms}")
    private Long refreshTokenDurationMs;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AppUserRepository userRepository;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            AppUserRepository userRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new AccessDeniedException("Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
