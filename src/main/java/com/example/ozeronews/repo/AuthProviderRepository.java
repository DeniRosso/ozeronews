package com.example.ozeronews.repo;

import com.example.ozeronews.models.AuthProvider;

public interface AuthProviderRepository {

    Iterable<AuthProvider> findAll();

    AuthProvider findById(Long id);
}
