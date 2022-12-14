package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.exception.TokenRefreshException;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.token.RefreshToken;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.repository.RefreshTokenRepository;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.util.Util;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.token.refresh.duration}")
    private Long refreshTokenDurationMs;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken createRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(Util.generateRandomUuid());
        refreshToken.setRefreshCount(0L);
        return refreshToken;
    }

    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            throw new TokenRefreshException(token.getToken(), "Expired token. Please issue a new request");
        }
    }

    public void deleteById(Long id) {
        refreshTokenRepository.deleteById(id);
    }

    public void increaseCount(RefreshToken refreshToken) {
        refreshToken.incrementRefreshCount();
        save(refreshToken);
    }
}
