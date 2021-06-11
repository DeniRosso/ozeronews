package com.example.ozeronews.repo;

import com.example.ozeronews.models.Role;

public interface RoleRepository {

    Iterable<Role> findAll();

    Role findById(Long id);
}
