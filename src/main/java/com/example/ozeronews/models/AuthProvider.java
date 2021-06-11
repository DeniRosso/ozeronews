package com.example.ozeronews.models;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum AuthProvider {
    LOCAL, GOOGLE, FACEBOOK;



    public Set<AuthProvider> getAuthProvider() {
        Set<AuthProvider> authProviders = new HashSet<>();
//        List<Role> roles = new ArrayList<>();
        authProviders.addAll(Arrays.asList(AuthProvider.values()));

        return authProviders;
    }
}
