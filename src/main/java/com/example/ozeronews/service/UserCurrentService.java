package com.example.ozeronews.service;

import com.example.ozeronews.models.User;
import com.example.ozeronews.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
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

    // Формирование userPicture
    public String getUserPicture(User user) {
        String userPicture = null;

        if (user.getPicture() != null) {
            try {
                userPicture = "data:image/png;base64," + new String(user.getPicture(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (user.getAvatar() != null) {
            userPicture = user.getAvatar();
        }  else {
            userPicture = "/static/images/login.png";
        }
        return userPicture;
    }
}
