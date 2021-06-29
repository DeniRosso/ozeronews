package com.example.ozeronews.controllers;

import com.example.ozeronews.config.AppConfig;
import com.example.ozeronews.models.Role;
import com.example.ozeronews.models.User;
import com.example.ozeronews.repo.RoleRepository;
import com.example.ozeronews.repo.UserRepo;
import com.example.ozeronews.repo.UserRepository;
import com.example.ozeronews.service.UserCurrentService;
import com.example.ozeronews.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Controller
@RequestMapping("")
public class UsersController {

    private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    private UserCurrentService userCurrentService;
    private UserRepository userRepository;
    private UserService userService;
    private RoleRepository roleRepository;
    private RestTemplate restTemplate;
    private AppConfig appConfig;

    @Autowired
    private UserRepo userRepo;

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${recaptcha.secret}")
    private String secret;

    @Autowired
    public UsersController(UserCurrentService userCurrentService,
                           UserRepository userRepository,
                           UserService userService,
                           RoleRepository roleRepository,
                           RestTemplate restTemplate,
                           AppConfig appConfig) {
        this.userCurrentService = userCurrentService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }

    // View all users
    @PreAuthorize(value = "hasAuthority('USER') OR hasAuthority('ADMIN')")
    @GetMapping("/administration/users")
    public String viewUsers(Principal principal, Model model) {

        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("roles", Role.values()); //model.addAttribute("roles", Role.class);
        model.addAttribute("userSelect", new User());

        model.addAttribute("currentPage", "adminUsers");
        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("user", userCurrentService.getCurrentUser(principal));
        return "users/users";
    }

    // View user
    //@PreAuthorize(value = "hasAuthority('ADMIN')")
    @GetMapping("/administration/users/edit/{id}")
    public String loadUser(Principal principal,
                           @PathVariable("id") Long id,
                           Model model) {

        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("roles", Role.values());
        model.addAttribute("userSelect", userRepository.findById(id));

        model.addAttribute("currentPage", "adminUsers");
        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("user", userCurrentService.getCurrentUser(principal));
        return "users/users";
    }

    // Edit data user
    @PostMapping("/administration/users/edit/{id}")
    public String editUser(Principal principal,
                           User userSelect,
                           Model model) {

        // Проверка существования пользователя по Email и/или Username
        // Если Email и/или Username уже существуют, нужно вывести сообщение
        User userExist;
        userExist = userRepository.findByEmail(userSelect.getEmail());
        if (userExist != null) {
            System.out.println("Email уже существует");

            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("roles", Role.values());
            model.addAttribute("userSelect", userRepository.findById(userSelect.getId()));

            model.addAttribute("currentPage", "adminUsers");
            model.addAttribute("head", appConfig.getHead());
            model.addAttribute("user", userCurrentService.getCurrentUser(principal));
            return "users/users";
        }
        userExist = userRepository.findByUsername(userSelect.getUsername());
        if (userExist != null) {
            System.out.println("Username уже существует");

            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("roles", Role.values());
            model.addAttribute("userSelect", userRepository.findById(userSelect.getId()));

            model.addAttribute("currentPage", "adminUsers");
            model.addAttribute("head", appConfig.getHead());
            model.addAttribute("user", userCurrentService.getCurrentUser(principal));
            return "users/users";
        }

        userSelect.setDateStamp(ZonedDateTime.now(ZoneId.of("UTC")));
        userRepo.save(userSelect);
        return "redirect:/administration/users";
    }

    // Remove user
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        //userDAO.deleteDBUser(id);
        System.out.println("user (@DeleteMapping({id}) = " + id);
        return "redirect:/logout";
    }



}
