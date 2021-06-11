package com.example.ozeronews.security;

import com.example.ozeronews.models.Role;
import com.example.ozeronews.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    private User user;

    public CustomUserDetails() {
    }

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public static CustomUserDetails create(User user) {
        List<GrantedAuthority> authorities = user.getRole().stream().map(role ->
                new SimpleGrantedAuthority(role.name())).collect(Collectors.toList());

        CustomUserDetails principalUser = new CustomUserDetails();
        principalUser.setId(user.getId());
        principalUser.setUsername(user.getUsername());
        principalUser.setEmail(user.getEmail());
        principalUser.setPassword(user.getPassword());
        principalUser.setAuthorities(user.getRole());

        return principalUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = user.getRole();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.name()));
        }
        System.out.println("authorities = " + authorities);
        return authorities;
//        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString());
//        return Arrays.asList(authority);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getName() {
        return user.getUsername();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
}
