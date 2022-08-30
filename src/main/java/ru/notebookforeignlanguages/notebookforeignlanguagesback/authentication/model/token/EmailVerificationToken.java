package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.TokenStatus;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.User;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.audit.DateAudit;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "EMAIL_VERIFICATION_TOKEN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationToken extends DateAudit {

    @Id
    @Column(name = "TOKEN_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_token_seq")
    @SequenceGenerator(name = "email_token_seq", allocationSize = 1)
    private Long id;

    @Column(name = "TOKEN", nullable = false, unique = true)
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "USER_ID")
    private User user;

    @Column(name = "TOKEN_STATUS")
    @Enumerated(EnumType.STRING)
    private TokenStatus tokenStatus;

    @Column(name = "EXPIRY_DT", nullable = false)
    private Instant expiryDate;


}
