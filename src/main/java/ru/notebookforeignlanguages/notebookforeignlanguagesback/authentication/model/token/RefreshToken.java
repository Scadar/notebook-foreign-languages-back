package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.UserDevice;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.audit.DateAudit;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "REFRESH_TOKEN")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken extends DateAudit {

    @Id
    @Column(name = "TOKEN_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refresh_token_seq")
    @SequenceGenerator(name = "refresh_token_seq", allocationSize = 1)
    private Long id;

    @Column(name = "TOKEN", nullable = false, unique = true)
    @NaturalId(mutable = true)
    private String token;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_DEVICE_ID", unique = true)
    private UserDevice userDevice;

    @Column(name = "REFRESH_COUNT")
    private Long refreshCount;

    @Column(name = "EXPIRY_DT", nullable = false)
    private Instant expiryDate;

    public void incrementRefreshCount() {
        refreshCount = refreshCount + 1;
    }
}
