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
public class PasswordResetRequest {

    @NotBlank(message = "The email for which the password needs to be reset can not be empty")
    private String email;

    @NotBlank(message = "New password cannot be blank")
    private String password;

    @NotBlank(message = "Confirm Password cannot be blank")
    private String confirmPassword;

    @NotBlank(message = "Password reset token for the specified email has to be supplied")
    private String token;

}
