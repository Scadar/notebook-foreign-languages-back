package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;


}
