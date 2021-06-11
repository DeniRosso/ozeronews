package com.example.ozeronews.controllers;

import com.example.ozeronews.models.User;
import com.example.ozeronews.repo.UserRepository;
import com.example.ozeronews.service.MailSenderService;
import com.example.ozeronews.service.UserCurrentService;
import com.example.ozeronews.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.util.StringUtils;

import java.security.Principal;

@Controller
@RequestMapping("")
public class UserActivateController {

    private UserCurrentService userCurrentService;
    private UserRepository userRepository;
    private UserService userService;
    private MailSenderService mailSenderService;

    @Autowired
    public UserActivateController(UserCurrentService userCurrentService,
                                  UserRepository userRepository,
                                  UserService userService,
                                  MailSenderService mailSenderService) {
        this.userCurrentService = userCurrentService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailSenderService = mailSenderService;
    }

    @GetMapping("/activate")
    public String requestActivate(Principal principal,
                                  Model model) {
        User currentUser = userCurrentService.getCurrentUser(principal);
        if (!StringUtils.isEmpty(principal.getName())) {
            if (!mailSenderService.mailActivateUser(currentUser)) {
                model.addAttribute("messageError",
                        "Неудалось отправить письмо для активации профиля. Попробуйте еще раз.");
            } else {
                model.addAttribute("message",
                        "Код активации потправлен на Email " + principal.getName());
            }
        }
        model.addAttribute("user", currentUser);
        return "users/profile";
    }

    @GetMapping("/activate/{code}")
    public String confirmActivate(@PathVariable String code,
                                  Model model) {
        Iterable<User> users = userRepository.findByActivationCode(code);
        for (User user : users) {
            if (userService.activateUser(code)) {
                model.addAttribute("messageType", "alert alert-success");
                model.addAttribute("message", "Активация успешна.");
            } else {
                model.addAttribute("messageType", "alert alert-danger");
                model.addAttribute("message", "Ошибка активании");
            }
        }
        model.addAttribute("user", new User());
        return "users/login";
    }
}
