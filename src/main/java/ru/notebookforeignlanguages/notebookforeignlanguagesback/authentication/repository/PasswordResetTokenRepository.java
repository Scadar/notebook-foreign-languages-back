package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.PasswordResetToken;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    @Query("SELECT t FROM PASSWORD_RESET_TOKEN t WHERE t.active = true and t.user = :user")
    List<PasswordResetToken> findActiveTokensForUser(User user);
}
