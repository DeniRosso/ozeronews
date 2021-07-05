package com.example.ozeronews.controllers;

import com.example.ozeronews.config.AppConfig;
import com.example.ozeronews.models.User;
import com.example.ozeronews.models.dto.CaptchaResponseDTO;
import com.example.ozeronews.repo.UserRepository;
import com.example.ozeronews.service.MailService;
import com.example.ozeronews.service.UserCurrentService;
import com.example.ozeronews.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Controller
@RequestMapping("")
public class UserRecoveryController {

    private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    private UserCurrentService userCurrentService;
    private UserRepository userRepository;
    private UserService userService;
    private MailService mailService;
    private RestTemplate restTemplate;
    private AppConfig appConfig;

    @Value("${recaptcha.secret}")
    private String secret;

    public UserRecoveryController(UserCurrentService userCurrentService,
                                  UserRepository userRepository,
                                  UserService userService,
                                  MailService mailService,
                                  RestTemplate restTemplate,
                                  AppConfig appConfig) {
        this.userCurrentService = userCurrentService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }

    // Recovery password
    @GetMapping("/recovery")
    public String viewRecovery(Model model) {

        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("user", new User());
        return "users/recovery";
    }

    @PostMapping("/recovery")
    public String sendRecoveryCode(@RequestParam("g-recaptcha-response") String captchaResponse,
                                   Locale locale,
                                   @Valid User user,
                                   BindingResult bindingResult,
                                   Model model,
                                   Errors errors) throws MessagingException {
        String url = String.format(CAPTCHA_URL, secret, captchaResponse);
        CaptchaResponseDTO responseDTO = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDTO.class);

        if (errors.hasFieldErrors("email") || !responseDTO.isSuccess()) {
            if (!responseDTO.isSuccess()) {
                model.addAttribute("captchaError", "Fill captcha");
            }
            if (errors.hasFieldErrors("email")) {

            }
            model.addAttribute("head", appConfig.getHead());
            model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
            model.addAttribute("user", user);
            return "users/recovery";
        }
        if (!userRepository.checkByEmail(user.getEmail())) {
            model.addAttribute("messageType", "alert alert-danger");
            model.addAttribute("message", "Пользователя с указанным адрессом электронной почты не существует.");
            model.addAttribute("head", appConfig.getHead());
            model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
            model.addAttribute("user", user);
            return "users/recovery";
        }
        user = userRepository.findByEmail(user.getEmail());
        user.setRecoveryCode(UUID.randomUUID().toString());
        userRepository.saveRecoveryCode(user);

        if (!StringUtils.isEmpty(user.getEmail())) {
            if (!mailService.recoveryUser(user, locale)) {
                model.addAttribute("messageType", "alert alert-danger");
                model.addAttribute("message", "Неудалось отправить письмо для восстановления пароля");
                model.addAttribute("head", appConfig.getHead());
                model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
                model.addAttribute("user", userRepository.findByAuthorization(user));
                return "users/recovery";
            } else {
                model.addAttribute("messageType", "alert alert-success");
                model.addAttribute("message", "Письмо для восстановления пароля отправленно на электронную почту.");
                model.addAttribute("head", appConfig.getHead());
                model.addAttribute("userPicture", userCurrentService.getUserPicture(new User()));
                model.addAttribute("user", new User());
                return "users/login";
            }
        }
        return "users/recovery";
    }

    @GetMapping("/recovery/{code}")
    public String viewSetNewPassword(@PathVariable String code, Model model) {

        List<User> users = userRepository.findByRecoveryCode(code);
        if (users.isEmpty()) {
            model.addAttribute("messageType", "alert alert-danger");
            model.addAttribute("message", "Пользователь не был найден.");
            model.addAttribute("head", appConfig.getHead());
            model.addAttribute("user", new User());
            return "users/login";
        }
        for (User recoveryUser : users) {
            if (recoveryUser.getRecoveryCode() != null) {
                model.addAttribute("messageType", "alert alert-secondary");
                model.addAttribute("message", "Восстановление пароля.");
                model.addAttribute("head", appConfig.getHead());
                model.addAttribute("recoveryUser", recoveryUser);
                model.addAttribute("user", new User());
                return "users/newpassword";
            } else {
                model.addAttribute("messageType", "alert alert-danger");
                model.addAttribute("message", "Пользователь не был найден.");
                model.addAttribute("head", appConfig.getHead());
                model.addAttribute("user", new User());
                return "users/login";
            }
        }
        return "users/recovery";
    }

    @PostMapping("/recovery/{code}")
    public String changePassword(@PathVariable String code,
                                  @Valid User recoveryUser,
                                  BindingResult bindingResult,
                                  Model model,
                                  Errors errors) {

        if (errors.hasFieldErrors("password")) {
            model.addAttribute("head", appConfig.getHead());
            model.addAttribute("user", new User());
            return "users/newpassword";
        }
        if (recoveryUser.getPassword() != null && !recoveryUser.getPassword().equals(recoveryUser.getPassword2())) {
            recoveryUser.setPassword(null);
            recoveryUser.setPassword2(null);
            model.addAttribute("passwordError", "Пароли не совпадают");
            model.addAttribute("password2Error", "Пароли не совпадают");
            model.addAttribute("head", appConfig.getHead());
            model.addAttribute("recoveryUser", recoveryUser);
            model.addAttribute("user", new User());
            return "users/newpassword";
        }
        if (userService.recoveryUser(recoveryUser, code)) {
            model.addAttribute("messageType", "alert alert-success");
            model.addAttribute("message", "Пароль изменен.");
            model.addAttribute("head", appConfig.getHead());
            model.addAttribute("user", new User());
            return "users/login";
        } else {
            recoveryUser.setPassword(null);
            recoveryUser.setPassword2(null);
            model.addAttribute("messageType", "alert alert-danger");
            model.addAttribute("message", "Не удалось изменить пароль.");
            model.addAttribute("head", appConfig.getHead());
            model.addAttribute("recoveryUser", recoveryUser);
            model.addAttribute("user", new User());
            return "users/newpassword";
        }
    }
}
