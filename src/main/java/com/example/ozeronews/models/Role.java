package com.example.ozeronews.models;

import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum Role implements GrantedAuthority {
    ADMIN ("Администратор"),
    USER ("Пользователь");


    private String displayValue;

    Role(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }


    @Override
    public String getAuthority() {
        return name();
    }


    public Set<Role> getRoles() {
        Set<Role> roles = new HashSet<>();
//        List<Role> roles = new ArrayList<>();
        roles.addAll(Arrays.asList(Role.values()));

        return roles;
    }


}
