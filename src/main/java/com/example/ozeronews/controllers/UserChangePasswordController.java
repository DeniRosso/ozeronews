package com.example.ozeronews.controllers;

import com.example.ozeronews.config.AppConfig;
import com.example.ozeronews.models.User;
import com.example.ozeronews.service.UserCurrentService;
import com.example.ozeronews.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import java.security.Principal;
import java.util.Locale;

@Controller
@RequestMapping("")
public class UserChangePasswordController {

    private UserCurrentService userCurrentService;
    private UserService userService;
    private AppConfig appConfig;

    @Autowired
    public UserChangePasswordController(UserCurrentService userCurrentService,
                                        UserService userService,
                                        AppConfig appConfig) {
        this.userCurrentService = userCurrentService;
        this.userService = userService;
        this.appConfig = appConfig;
    }

    @GetMapping("/users/change-password")
    public String viewChangePassword(Principal principal, Model model) {

        User user = userCurrentService.getCurrentUser(principal);

        model.addAttribute("password", "");
        model.addAttribute("newPassword", "");
        model.addAttribute("newPassword2", "");

        model.addAttribute("currentPage", "userChangePassword");
        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
        model.addAttribute("user", user);
        return "users/change-password";
    }

    @PostMapping("/users/change-password")
    public String changePassword(Principal principal,
                                 Locale locale,
                                 @RequestParam("password") String password,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("newPassword2") String newPassword2,
                                 Model model) throws MessagingException {

        User user = userCurrentService.getCurrentUser(principal);
        model.addAttribute("password", "");
        model.addAttribute("newPassword", "");
        model.addAttribute("newPassword2", "");

        model.addAttribute("currentPage", "userChangePassword");
        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
        model.addAttribute("user", user);

//        if (!passwordEncoder.matches(password, user.getPassword())) {
        if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            model.addAttribute("passwordError", "Неверный текущий пароль.");
            return "users/change-password";
        }
        if (!newPassword.equals(newPassword2)) {
            model.addAttribute("newPasswordError", "Пароли не совпадают");
            model.addAttribute("newPassword2Error", "Пароли не совпадают");
            return "users/change-password";
        }

        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        if (!userService.changePassword(user, locale)) {
            model.addAttribute("messageType", "alert alert-danger");
            model.addAttribute("message", "Пароль не удалось изменить. Попробуйте еще раз.");
        } else {
            model.addAttribute("messageType", "alert alert-success");
            model.addAttribute("message", "Пароль успешно изменен.");
        }
        return "users/change-password";
    }
}
