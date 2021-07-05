package com.example.ozeronews.controllers;

import com.example.ozeronews.config.AppConfig;
import com.example.ozeronews.models.AuthProvider;
import com.example.ozeronews.models.User;
import com.example.ozeronews.repo.UserRepo;
import com.example.ozeronews.repo.UserRepository;
import com.example.ozeronews.service.UserCurrentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/users")
public class UserDeleteController {

    private UserCurrentService userCurrentService;
    private UserRepository userRepository;
    private UserRepo userRepo;
    private AppConfig appConfig;

    @Autowired
    public UserDeleteController(UserCurrentService userCurrentService,
                                UserRepository userRepository,
                                UserRepo userRepo,
                                AppConfig appConfig) {
        this.userCurrentService = userCurrentService;
        this.userRepository = userRepository;
        this.userRepo = userRepo;
        this.appConfig = appConfig;
    }

    @GetMapping("/delete")
    public String viewDeleteUser(Principal principal, Model model) {

        User user = userCurrentService.getCurrentUser(principal);

        if (user.getAuthProvider().stream().findFirst().get().equals(AuthProvider.LOCAL)) {
            model.addAttribute("provider", null);
        } else {
            model.addAttribute("provider", user.getAuthProvider().stream().findFirst().get());
        }
        model.addAttribute("currentPage", "userDelete");
        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
        model.addAttribute("user", user);
        return "users/delete";
    }

    @PostMapping("/delete")
    public String deleteUser(Principal principal,
                             HttpServletRequest request,
                             SessionStatus session,
                             @Valid User user,
                             BindingResult bindingResult,
                             Model model,
                             Errors errors) {

        if (errors.hasFieldErrors("password")) {
            model.addAttribute("passwordError", "Введите пароль");
            model.addAttribute("currentPage", "userDelete");
            model.addAttribute("head", appConfig.getHead());
            model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
            model.addAttribute("user", user);
            return "users/delete";
        }
        
        User currentUser = userCurrentService.getCurrentUser(principal);
//        if (!passwordEncoder.matches(user.getPassword(), currentUser.getPassword())) {
        if (!new BCryptPasswordEncoder().matches(user.getPassword(), currentUser.getPassword())) {
            user.setPassword(null);
            model.addAttribute("passwordError", "Введен неверный пароль.");
            model.addAttribute("currentPage", "userDelete");
            model.addAttribute("head", appConfig.getHead());
            model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
            model.addAttribute("user", user);
            return "users/delete";
        }
        session.setComplete();
        request.getSession().invalidate();
        currentUser.setActive(false);
        userRepository.updateActive(currentUser);
//        userRepo.delete(currentUser);
        model.addAttribute("messageType", "alert alert-success");
        model.addAttribute("message", "Пользователь удален");
        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("userPicture", userCurrentService.getUserPicture(new User()));
        model.addAttribute("user", new User());
        return "users/login";
    }
}
