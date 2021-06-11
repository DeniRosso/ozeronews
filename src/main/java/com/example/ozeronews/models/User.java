package com.example.ozeronews.models;

import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.*;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotNull(message = "Введите значение")
    @NotEmpty(message = "Укажите значение")
    @Size(max=30, message = "Максимальная длина 30 символом")
    @Column(length = 45)
    private String username;

    @NotEmpty(message = "Укажите значение")
    @Size(max=80, message = "Максимальная длина 80 символом")
    @Email(message = "Некорректный адресс электронной почты")
    @Column(unique = true, length = 80)
    private String email;

    @NotEmpty(message = "Укажите значение")
    @Column(length = 80)
    private String password;

    @Transient
//    @NotEmpty(message = "Укажите значение")
    private String password2;

    @Column(length = 45)
    private String firstname;

    @Column(length = 45)
    private String lastname;

    @Column(length = 120)
    private String avatar;

    @Column(length = 45)
    private String activationCode;

    @Column(length = 45)
    private String recoveryCode;
    private boolean active;
    private ZonedDateTime dateStamp;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "users_roles", joinColumns = @JoinColumn(name = "id"))
    private Set<Role> role = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = AuthProvider.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "auth_providers", joinColumns = @JoinColumn(name = "id"))
    private Set<AuthProvider> authProvider = new HashSet<>();

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Subscription> subscription = new ArrayList<>();


//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return getRole();
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }


    public User() {
    }

    public User toUpdateUser(PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setUsername(username);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setLastname(avatar);
        user.setRole(Collections.singleton(Role.USER));
        return user;
    }

}
