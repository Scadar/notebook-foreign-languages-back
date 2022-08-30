package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.Role;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.RoleName;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.repository.RoleRepository;

import java.util.Collection;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Collection<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role findByRoleName(RoleName roleName) {
        return roleRepository.findByRole(roleName).orElseThrow(() -> new RuntimeException("Role not found"));
    }

}
