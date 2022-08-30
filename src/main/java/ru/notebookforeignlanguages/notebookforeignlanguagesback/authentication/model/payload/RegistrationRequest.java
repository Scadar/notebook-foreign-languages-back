package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.validation.annotation.MatchPassword;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.validation.annotation.NullOrNotBlank;

import javax.validation.constraints.NotNull;

@MatchPassword
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

    @NullOrNotBlank(message = "Registration username can be null but not blank")
    private String username;

    @NullOrNotBlank(message = "Registration email can be null but not blank")
    private String email;

    @NotNull(message = "Registration password cannot be null")
    private String password;

}
