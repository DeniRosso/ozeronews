package com.example.ozeronews.service;

import com.example.ozeronews.models.User;
import com.example.ozeronews.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class UserCurrentService {

    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser(Principal principal) {
        User user = new User();
        if (principal != null) {
            if (userRepository.checkByEmail(principal.getName())) {
                user = userRepository.findByEmail(principal.getName());
            }
        }
        return user;
    }
}
