package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model;

import lombok.*;
import org.hibernate.annotations.NaturalId;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.audit.DateAudit;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.validation.annotation.NullOrNotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "USERS")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User extends DateAudit {

    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", allocationSize = 1)
    private Long id;

    @NaturalId
    @Column(name = "EMAIL", unique = true)
    @NotBlank(message = "User email cannot be null")
    private String email;

    @Column(name = "USERNAME", unique = true)
    @NullOrNotBlank(message = "Username can not be blank")
    private String username;

    @Column(name = "PASSWORD")
    @NotNull(message = "Password cannot be null")
    private String password;

    @Column(name = "FIRST_NAME")
    @NullOrNotBlank(message = "First name can not be blank")
    private String firstName;

    @Column(name = "LAST_NAME")
    @NullOrNotBlank(message = "Last name can not be blank")
    private String lastName;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean active;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "USER_AUTHORITY", joinColumns = {
            @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")}, inverseJoinColumns = {
            @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")})
    private Set<Role> roles = new HashSet<>();

    @Column(name = "IS_EMAIL_VERIFIED", nullable = false)
    private Boolean isEmailVerified;

    public User(User user) {
        id = user.getId();
        username = user.getUsername();
        password = user.getPassword();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        email = user.getEmail();
        active = user.getActive();
        roles = user.getRoles();
        isEmailVerified = user.getIsEmailVerified();
    }

    public void addRole(Role role) {
        roles.add(role);
        role.getUserList().add(this);
    }

    public void markVerificationConfirmed() {
        setIsEmailVerified(true);
    }

}
