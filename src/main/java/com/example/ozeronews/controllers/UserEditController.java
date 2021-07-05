package com.example.ozeronews.controllers;

import com.example.ozeronews.config.AppConfig;
import com.example.ozeronews.models.User;
import com.example.ozeronews.repo.UserRepo;
import com.example.ozeronews.repo.UserRepository;
import com.example.ozeronews.service.UserCurrentService;
import com.example.ozeronews.service.UserService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class UserEditController {

    private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    private UserCurrentService userCurrentService;
    private UserRepo userRepo;
    private UserRepository userRepository;
    private UserService userService;
    private RestTemplate restTemplate;
    private AppConfig appConfig;

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${recaptcha.secret}")
    private String secret;

    @Autowired
    public UserEditController(UserCurrentService userCurrentService,
                              UserRepo userRepo,
                              UserRepository userRepository,
                              UserService userService,
                              RestTemplate restTemplate,
                              AppConfig appConfig) {
        this.userCurrentService = userCurrentService;
        this.userRepo = userRepo;
        this.userRepository = userRepository;
        this.userService = userService;
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }

    // View edit user
    @GetMapping("/edit")
    public String editUser(Principal principal, Model model) {

        User user = userCurrentService.getCurrentUser(principal);

        model.addAttribute("currentPage", "userEdit");
        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
        model.addAttribute("user", user);
        return "users/edit";
    }

    // Update data user
    @PostMapping("/edit")
    public String updateUser(Principal principal,
                             @Valid User user,
                             BindingResult bindingResult,
                             Model model,
                             @RequestParam("file") MultipartFile file) throws IOException {

        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("currentPage", "userEdit");
            model.addAttribute("head", appConfig.getHead());
            model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
            model.addAttribute("user", user);
            return "users/edit";
        }
        User currentUser = userCurrentService.getCurrentUser(principal);
//        if (!passwordEncoder.matches(user.getPassword(), currentUser.getPassword())) {
        if (!new BCryptPasswordEncoder().matches(user.getPassword(), currentUser.getPassword())) {
            user.setPassword(null);
            model.addAttribute("passwordError", "Введен неверный пароль.");
            model.addAttribute("currentPage", "userEdit");
            model.addAttribute("head", appConfig.getHead());
            model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
            model.addAttribute("user", user);
            return "users/edit";
        }

        if (!file.isEmpty()) {
//            Byte[] bytesPicture = new Byte[file.getBytes().length];
//            for (int i = 0; i < file.getBytes().length; i++) {
//                bytesPicture[i] = file.getBytes()[i];
//            }
//            user.setPicture(bytesPicture);

            byte[] encodeBase64 = Base64.encodeBase64(file.getBytes());
            user.setPicture(encodeBase64);
        }

//        if (!file.isEmpty()) {
//            System.out.println("!file.isEmpty()");
//            // Сохранение файла картинки
//            String currentFilename = null;
//            // Проверка существовани пользователя и картинки у него
//            User existUser = userRepository.findByEmail(user.getEmail());
//            if (existUser != null) {
//                currentFilename = existUser.getAvatar();
//                // !!! Нужно проверить равенство станого и нового файлов
//            }
//            // Сохранение файла
//            user.setAvatar("/img/" + userService.addAvatar(file));
////            user.setAvatar(userService.addAvatar(file));
//
//            if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
//                model.addAttribute("messageType", "alert alert-danger");
//                model.addAttribute("message",
//                        "Не удалось сохранить файл '" + file.getName() + "'. Попробуйте еще раз.");
//                user.setPassword(null);
//                model.addAttribute("currentPage", "userEdit");
//                model.addAttribute("head", appConfig.getHead());
//                model.addAttribute("user", user);
//                return "users/edit";
//            }
//            // Удаление файла предыдущей картинки после сохранения нового файла
//            if (currentFilename != null) {
//                // !!! Нужно удалить файл с диска
//            }
//        } else {
//            user.setAvatar(currentUser.getAvatar());
//        }


        model.addAttribute("messageType", "alert alert-success");
        model.addAttribute("message", "Данные успешно изменены.");
        System.out.println("editUser = " + user);
        userService.editUser(currentUser, user);
        return "redirect:/users";
    }
}
