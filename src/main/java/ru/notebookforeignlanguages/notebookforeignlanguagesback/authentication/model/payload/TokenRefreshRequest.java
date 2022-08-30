package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.validation.annotation.MatchPassword;

import javax.validation.constraints.NotBlank;

@MatchPassword
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenRefreshRequest {

    @NotBlank(message = "Refresh token cannot be blank")
    private String refreshToken;

}
