package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.Role;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.service.RoleService;

import java.util.Collection;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Collection<Role>> loadAllRoles() {
        return ResponseEntity.ok(roleService.findAll());
    }

}
