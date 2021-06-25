package com.example.ozeronews.controllers;

import com.example.ozeronews.config.AppConfig;
import com.example.ozeronews.models.AuthProvider;
import com.example.ozeronews.models.Subscription;
import com.example.ozeronews.models.User;
import com.example.ozeronews.repo.SubscriptionRepository;
import com.example.ozeronews.repo.UserRepo;
import com.example.ozeronews.service.SubscriptionService;
import com.example.ozeronews.service.UserCurrentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/users/subscriptions")
public class SubscriptionController {

    private UserCurrentService userCurrentService;
    private UserRepo userRepo;
    private SubscriptionRepository subscriptionRepository;
    private SubscriptionService subscriptionService;
    private AppConfig appConfig;

    @Autowired
    public SubscriptionController(UserCurrentService userCurrentService,
                                  UserRepo userRepo,
                                  SubscriptionRepository subscriptionRepository,
                                  SubscriptionService subscriptionService,
                                  AppConfig appConfig) {
        this.userCurrentService = userCurrentService;
        this.userRepo = userRepo;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionService = subscriptionService;
        this.appConfig = appConfig;
    }

    @GetMapping("")
    public String viewSubscription(Principal principal, Model model) {

        User user = userCurrentService.getCurrentUser(principal);
        if (user.getAuthProvider().stream().findFirst().get().equals(AuthProvider.LOCAL)) {
            model.addAttribute("provider", null);
        } else {
            model.addAttribute("provider", user.getAuthProvider().stream().findFirst().get());
        }
        model.addAttribute("subscriptions", subscriptionService.findAllByUser(userRepo.findByEmail(principal.getName())));
        model.addAttribute("currentPage", "userSubscriptions");
        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("user", userCurrentService.getCurrentUser(principal));
        return "subscriptions";
    }

    @PostMapping("")
    public String updateSubscribe(Subscription subscription) {
        subscriptionService.createSubscription(subscription);
        return ("redirect:/users/subscriptions");
    }

}
