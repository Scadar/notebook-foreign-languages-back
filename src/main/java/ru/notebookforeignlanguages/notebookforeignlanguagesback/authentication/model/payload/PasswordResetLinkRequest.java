package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetLinkRequest {

    @NotBlank(message = "Email cannot be blank")
    private String email;

}
