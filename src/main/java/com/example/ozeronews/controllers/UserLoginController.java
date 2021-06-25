package com.example.ozeronews.controllers;

import com.example.ozeronews.config.AppConfig;
import com.example.ozeronews.models.Head;
import com.example.ozeronews.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class UserLoginController {

    private AppConfig appConfig;

    @Autowired
    public UserLoginController(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    // View login form
    @GetMapping("/login")
    public String loginUser(Model model) {

        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("user", new User());
        return "users/login";
    }
}
