package com.example.ozeronews.repo;

import com.example.ozeronews.models.User;

import java.util.List;

public interface UserRepository {

    Iterable<User> findAll();

    User findById(Long id);

    User findByEmail(String email);

    User findByAuthorization(User user);

    User findByUsername(String username);

    Iterable<User> findByActivationCode(String activationCode);

    List<User> findByRecoveryCode(String recoveryCode);

    boolean checkByEmail(String email);

    boolean checkByUsername(String username);

    int[] save(User user);

    int[] updateUser(User user);

    int[] updatePassword(User user);

    int[] updateActive(User user);

    int[] saveRecoveryCode(User user);
}
