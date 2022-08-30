package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.validation.annotation.NullOrNotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NullOrNotBlank(message = "Login Username can be null but not blank")
    private String username;

    @NullOrNotBlank(message = "Login Email can be null but not blank")
    private String email;

    @NotNull(message = "Login password cannot be blank")
    private String password;

    @Valid
    @NotNull(message = "Device info cannot be null")
    private DeviceInfo deviceInfo;

}
