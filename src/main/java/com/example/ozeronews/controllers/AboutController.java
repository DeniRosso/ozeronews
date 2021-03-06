package com.example.ozeronews.controllers;

import com.example.ozeronews.config.AppConfig;
import com.example.ozeronews.models.Contact;
import com.example.ozeronews.models.User;
import com.example.ozeronews.models.dto.CaptchaResponseDTO;
import com.example.ozeronews.repo.UserRepository;
import com.example.ozeronews.service.MailService;
import com.example.ozeronews.service.UserCurrentService;
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
import org.thymeleaf.util.StringUtils;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Collections;
import java.util.Locale;

@Controller
@RequestMapping("")
public class AboutController {

    private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    private UserRepository userRepository;
    private UserCurrentService userCurrentService;
    private MailService mailService;
    private RestTemplate restTemplate;
    private AppConfig appConfig;

    @Value("${website.url}")
    private String websiteBaseURL;

    @Value("${website.name}")
    private String websiteName;

    @Value("${company.name}")
    private String companyName;

    @Value("${recaptcha.secret}")
    private String secret;

    @Autowired
    public AboutController(UserRepository userRepository,
                           UserCurrentService userCurrentService,
                           MailService mailService,
                           RestTemplate restTemplate,
                           AppConfig appConfig) {
        this.userRepository = userRepository;
        this.userCurrentService = userCurrentService;
        this.mailService = mailService;
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }

    @GetMapping("/about")
    public String viewAbout(Principal principal, Model model) {

        User user = userCurrentService.getCurrentUser(principal);
        model.addAttribute("currentPage", "about");
        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
        model.addAttribute("user", user);
        return "about";
    }
    @GetMapping("/about/privacy")
    public String viewPrivacy(Principal principal, Model model) {

        User user = userCurrentService.getCurrentUser(principal);
        model.addAttribute("currentPage", "privacy");
        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
        model.addAttribute("user", user);
        return "privacy";
    }

    @GetMapping("/about/termsofuse")
    public String viewTermsofuse(Principal principal, Model model) {

        User user = userCurrentService.getCurrentUser(principal);
        model.addAttribute("currentPage", "useragreement");
        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
        model.addAttribute("user", user);
        return "termsofuse";
    }

    @GetMapping("/about/contact")
    public String viewContact(Principal principal,
                              Model model) {

        User user = userCurrentService.getCurrentUser(principal);
        model.addAttribute("contact", new Contact());

        model.addAttribute("currentPage", "contact");
        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
        model.addAttribute("user", user);
        return "contact";
    }

    @PostMapping("/about/contact")
    public String sendContactMessage(Principal principal,
              Locale locale,
              @RequestParam("g-recaptcha-response") String captchaResponse,
              @Valid Contact contact,
              BindingResult bindingResult,
              Model model,
              Errors errors) throws MessagingException {

        String url = String.format(CAPTCHA_URL, secret, captchaResponse);
        CaptchaResponseDTO responseDTO = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDTO.class);

        User user = userCurrentService.getCurrentUser(principal);

        if (errors.hasErrors() || !responseDTO.isSuccess()) {
            if (!responseDTO.isSuccess()) {
                model.addAttribute("captchaError", "Fill captcha");
            }
            if (errors.hasErrors()) {

            }
            model.addAttribute("contact", contact);
            model.addAttribute("currentPage", "contact");
            model.addAttribute("head", appConfig.getHead());
            model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
            model.addAttribute("user", user);
            return "contact";
        }
        if (!StringUtils.isEmpty(contact.getEmail())) {
            if (!mailService.contact(contact, locale)) {
                model.addAttribute("messageType", "alert alert-danger");
                model.addAttribute("message", "?????????????????? ?????????????????? ??????????????????. ???????????????????? ?????? ??????.");
                model.addAttribute("contact", contact);
                model.addAttribute("currentPage", "contact");
                model.addAttribute("head", appConfig.getHead());
                model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
                model.addAttribute("user", user);
                return "contact";
            } else {
                model.addAttribute("messageType", "alert alert-success");
                model.addAttribute("message", "?????????????????? ????????????????????. ?????????????? ???? ???????? ??????????????????.");
                model.addAttribute("contact", new Contact());
                model.addAttribute("currentPage", "contact");
                model.addAttribute("head", appConfig.getHead());
                model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
                model.addAttribute("user", user);
                return "contact";
            }
        }
        return "redirect:/contact";
    }
}
