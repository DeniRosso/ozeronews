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

import java.security.Principal;

@Controller
@RequestMapping("/users")
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

    @GetMapping("/changepassword")
    public String viewChangePassword(Principal principal, Model model) {

        model.addAttribute("currentPage", "userChangePassword");

        model.addAttribute("password", "");
        model.addAttribute("newPassword", "");
        model.addAttribute("newPassword2", "");

        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("user", userCurrentService.getCurrentUser(principal));
        return "users/changepassword";
    }

    @PostMapping("/changepassword")
    public String changePassword(Principal principal,
                                 @RequestParam String password,
                                 @RequestParam String newPassword,
                                 @RequestParam String newPassword2,
                                 Model model) {

        User user = userCurrentService.getCurrentUser(principal);

//        if (!passwordEncoder.matches(password, user.getPassword())) {
        if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            model.addAttribute("passwordError", "Неверный текущий пароль.");
            model.addAttribute("currentPage", "userChangePassword");
            model.addAttribute("head", appConfig.getHead());
            model.addAttribute("user", user);
            return "users/changepassword";
        }
        if (!newPassword.equals(newPassword2)) {
            model.addAttribute("newPasswordError", "Пароли не совпадают");
            model.addAttribute("newPassword2Error", "Пароли не совпадают");
            model.addAttribute("currentPage", "userChangePassword");
            model.addAttribute("head", appConfig.getHead());
            model.addAttribute("user", user);
            return "users/changepassword";
        }
        if (!userService.changePassword(user, newPassword)) {
            model.addAttribute("messageType", "alert alert-danger");
            model.addAttribute("message", "Пароль не удалось изменить. Попробуйте еще раз.");
            model.addAttribute("currentPage", "userChangePassword");
            model.addAttribute("head", appConfig.getHead());
            model.addAttribute("user", user);
            return "users/changepassword";
        }
        model.addAttribute("messageType", "alert alert-success");
        model.addAttribute("message", "Пароль успешно изменен.");
        model.addAttribute("currentPage", "userChangePassword");
        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("user", user);
        return "users/changepassword";
    }
}
