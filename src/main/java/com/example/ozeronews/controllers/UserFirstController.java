package com.example.ozeronews.controllers;

import com.example.ozeronews.config.AppConfig;
import com.example.ozeronews.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("")
@ResponseBody
public class UserFirstController {

    @Autowired
    private UserService userService;

    @GetMapping("/about/user")
    public String createFirstUser() {

        userService.createFirstUser();

        return "User is create";
    }
}
