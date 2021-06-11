package com.example.ozeronews.repo;

import com.example.ozeronews.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Long> {

    User save(User user);

    User findByEmail(String name);

    User findByUsername(String name);

    User findByAvatar(String name);

    User findByActivationCode(String code);
}
