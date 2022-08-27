package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test2")
public class Test2Controller {
    @GetMapping("/all")
    public String allAccess() {
        return "!!!!!!!";
    }
}
