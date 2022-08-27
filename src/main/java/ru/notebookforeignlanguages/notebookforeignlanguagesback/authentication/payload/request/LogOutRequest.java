package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.payload.request;

import lombok.Data;

@Data
public class LogOutRequest {
    private Long userId;
}
