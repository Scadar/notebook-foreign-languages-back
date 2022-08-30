package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateLastNameRequest {
    private String lastName;
}
