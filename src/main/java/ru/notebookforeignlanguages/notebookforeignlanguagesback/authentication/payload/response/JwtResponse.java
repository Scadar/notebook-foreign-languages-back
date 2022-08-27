package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JwtResponse {
    private final String type = "Bearer";
    private String token;
    private String refreshToken;
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
}
