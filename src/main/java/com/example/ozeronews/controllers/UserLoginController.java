package com.example.ozeronews.controllers;

import com.example.ozeronews.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class UserLoginController {

    // View login form
    @GetMapping("/login")
    public String loginUser(Model model) {
        model.addAttribute("user", new User());
        return "users/login";
    }
}
