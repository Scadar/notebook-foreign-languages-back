package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.payload;

import lombok.*;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.Role;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeResponse {
    private Long id;

    private String email;

    private String username;

    private String firstName;

    private String lastName;

    private Boolean active;

    private Set<Role> roles = new HashSet<>();
}
