package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.models.AppRole;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.models.ERole;

import java.util.Optional;

@Repository
public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
    Optional<AppRole> findByName(ERole name);
}
