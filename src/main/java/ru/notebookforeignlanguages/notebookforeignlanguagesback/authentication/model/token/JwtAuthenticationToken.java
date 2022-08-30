package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.token;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@Setter
public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private String token;

    public JwtAuthenticationToken(
            Object principal,
            Object credentials,
            String token
    ) {
        super(null, null);
        this.token = token;
    }

}
