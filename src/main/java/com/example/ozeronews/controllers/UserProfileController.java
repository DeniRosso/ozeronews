package com.example.ozeronews.controllers;

import com.example.ozeronews.models.AuthProvider;
import com.example.ozeronews.models.User;
import com.example.ozeronews.repo.UserRepo;
import com.example.ozeronews.repo.UserRepository;
import com.example.ozeronews.service.SubscriptionService;
import com.example.ozeronews.service.UserCurrentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/users")
public class UserProfileController {

    private UserCurrentService userCurrentService;
    private UserRepository userRepository;
    private UserRepo userRepo;
    private SubscriptionService subscriptionService;

    @Autowired
    public UserProfileController(UserCurrentService userCurrentService,
                                 UserRepository userRepository,
                                 UserRepo userRepo,
                                 SubscriptionService subscriptionService) {
        this.userCurrentService = userCurrentService;
        this.userRepository = userRepository;
        this.userRepo = userRepo;
        this.subscriptionService = subscriptionService;
    }

    // View account form with user
    @GetMapping("")
    public String viewUser(Principal principal,
                           Model model) {
        if (userRepo.findByEmail(principal.getName()).getActivationCode() != null) {
            model.addAttribute("messageError", "Требуется активация учетной записи.");

        }
        User user = userCurrentService.getCurrentUser(principal);
        if (user.getAuthProvider().stream().findFirst().get().equals(AuthProvider.LOCAL)) {
            model.addAttribute("provider", null);
        } else {
            model.addAttribute("provider", user.getAuthProvider().stream().findFirst().get());
        }
        model.addAttribute("subscriptions", subscriptionService.findByUser(userRepo.findByEmail(principal.getName())));
        model.addAttribute("currentPage", "userProfile");
        model.addAttribute("user", user);
        return "users/profile";
    }
}
