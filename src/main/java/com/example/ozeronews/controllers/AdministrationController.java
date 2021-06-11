package com.example.ozeronews.controllers;

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

    @GetMapping()
    public String viewAdministration(Principal principal,
                                     Model model) {

        model.addAttribute("currentPage", "administration");
        model.addAttribute("user", userCurrentService.getCurrentUser(principal));

        return "administration";
    }
}
