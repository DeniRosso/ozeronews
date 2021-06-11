package com.example.ozeronews.controllers;

import com.example.ozeronews.models.AuthProvider;
import com.example.ozeronews.models.User;
import com.example.ozeronews.models.dto.CaptchaResponseDTO;
import com.example.ozeronews.repo.UserRepo;
import com.example.ozeronews.repo.UserRepository;
import com.example.ozeronews.service.MailSenderService;
import com.example.ozeronews.service.UserCurrentService;
import com.example.ozeronews.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("")
public class UserRegistrationController {

    private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    private UserCurrentService userCurrentService;
    private UserRepo userRepo;
    private UserRepository userRepository;
    private UserService userService;
    private MailSenderService mailSenderService;
    private RestTemplate restTemplate;

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${recaptcha.secret}")
    private String secret;

    @Autowired
    public UserRegistrationController(UserCurrentService userCurrentService,
                                      UserRepo userRepo,
                                      UserRepository userRepository,
                                      UserService userService,
                                      MailSenderService mailSenderService,
                                      RestTemplate restTemplate) {
        this.userCurrentService = userCurrentService;
        this.userRepo = userRepo;
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailSenderService = mailSenderService;
        this.restTemplate = restTemplate;
    }

    // View registration form
    @GetMapping("/registration")
    public String viewRegistrationUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("newUser", new User());
        return "users/registration";
    }

    // Registration a new user
    @PostMapping("/registration")
    public String addNewUser(@RequestParam("g-recaptcha-response") String captchaResponse,
                             @Valid User newUser,
                             BindingResult bindingResult,
                             Model model,
                             Errors errors,
                             @RequestParam("file") MultipartFile file) throws IOException {

        String url = String.format(CAPTCHA_URL, secret, captchaResponse);
        CaptchaResponseDTO responseDTO = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDTO.class);

        if (bindingResult.hasErrors() || !responseDTO.isSuccess() || errors.hasErrors()) {
            if (!responseDTO.isSuccess()) {
                System.out.println("responseDTO = " + responseDTO.getErrorCodes());
                model.addAttribute("captchaError", "Fill captcha");
            }
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("user", new User());
            model.addAttribute("newUser", newUser);
            return "users/registration";
        }
        if (newUser.getPassword() != null && !newUser.getPassword().equals(newUser.getPassword2())) {
            newUser.setPassword(null);
            newUser.setPassword2(null);
            model.addAttribute("passwordError", "Пароли не совпадают");
            model.addAttribute("password2Error", "Пароли не совпадают");
            model.addAttribute("user", new User());
            model.addAttribute("newUser", newUser);
            return "users/registration";
        }
        // Проверка существования пользователя по Email
        User existUser = new User();
        if (userRepository.checkByEmail(newUser.getEmail())) {
            existUser = userRepository.findByEmail(newUser.getEmail());
            if (existUser.getAuthProvider().stream().findFirst().get().equals(AuthProvider.valueOf("LOCAL"))) {
                model.addAttribute("messageType", "alert alert-danger");
                model.addAttribute("message", "Пользователь с Email '" + newUser.getEmail() + "' уже существует");
            } else {
                model.addAttribute("messageType", "alert alert-danger");
                model.addAttribute("message", "Пользователь с Email '" + newUser.getEmail() +
                        "' уже зарегистрирован через " + existUser.getAuthProvider().stream().findFirst().get());
            }
            newUser.setPassword(null);
            newUser.setPassword2(null);
            model.addAttribute("user", new User());
            model.addAttribute("newUser", newUser);
            return "users/registration";
        }
        if (!file.isEmpty()) {
            // Сохранение файла картинки
            String currentFilename = null;
            // Проверка существовани пользователя и картинки у него
            if (existUser != null) {
                currentFilename = existUser.getAvatar();
                // !!! Нужно проверить равенство станого и нового файлов
            }
            // Сохранение файла
            newUser.setAvatar("/img/" + userService.addAvatar(file));
            if (newUser.getAvatar() == null || newUser.getAvatar().isEmpty()) {
                model.addAttribute("messageType", "alert alert-danger");
                model.addAttribute("message",
                        "Не удалось сохранить файл '" + file.getName() + "'. Попробуйте еще раз.");
                newUser.setPassword(null);
                newUser.setPassword2(null);
                model.addAttribute("user", new User());
                model.addAttribute("newUser", newUser);
                return "users/registration";
            }
            // Удаление файла предыдущей картинки после сохранения нового файла
            if (currentFilename != null) {
                // !!! Нужно удалить файл с диска
            }
        }
        if (!StringUtils.isEmpty(newUser.getEmail())) {
            if (!mailSenderService.mailActivateUser(newUser)) {
                model.addAttribute("messageType", "alert alert-danger");
                model.addAttribute("message", "Неудалось отправить письмо для активации профиля. Попробуйте еще раз.");
                newUser.setPassword(null);
                newUser.setPassword2(null);
                model.addAttribute("user", new User());
                model.addAttribute("newUser", newUser);
                return "users/registration";
            };
        }
        model.addAttribute("messageType", "alert alert-success");
        model.addAttribute("message", "Пользователь создан.");
        model.addAttribute("user", new User());
        model.addAttribute("newUser", newUser);
        userService.saveNewUser(newUser);
        return "users/registrationsuccess";
    }

    // View registration form
    @GetMapping("/registrationsuccess")
    public String viewRegistrationUserSuccess(Model model) {
        model.addAttribute("user", new User());
        return "users/registrationsuccess";
    }
}
