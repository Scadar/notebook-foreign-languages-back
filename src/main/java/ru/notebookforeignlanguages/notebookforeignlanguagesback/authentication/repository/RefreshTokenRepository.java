package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.models.AppUser;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.models.RefreshToken;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUser(AppUser user);
}
