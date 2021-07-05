package com.example.ozeronews.controllers;

import com.example.ozeronews.config.AppConfig;
import com.example.ozeronews.models.User;
import com.example.ozeronews.service.UserCurrentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/administration")
public class AdministrationController {

    @Autowired
    private UserCurrentService userCurrentService;

    @Autowired
    private AppConfig appConfig;

    @GetMapping()
    public String viewAdministration(Principal principal, Model model) {

        User user = userCurrentService.getCurrentUser(principal);

        model.addAttribute("currentPage", "administration");
        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
        model.addAttribute("user", user);
        return "administration";
    }
}
