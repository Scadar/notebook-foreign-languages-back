package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.UserDevice;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.token.RefreshToken;

import java.util.Optional;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    Optional<UserDevice> findById(Long id);

    Optional<UserDevice> findByRefreshToken(RefreshToken refreshToken);

    Optional<UserDevice> findByUserId(Long userId);
}
