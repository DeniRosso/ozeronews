package com.example.ozeronews.controllers;

import com.example.ozeronews.config.AppConfig;
import com.example.ozeronews.models.Rubric;
import com.example.ozeronews.models.User;
import com.example.ozeronews.repo.RubricRepository;
import com.example.ozeronews.service.UserCurrentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Controller
@RequestMapping("")
public class RubricController {

    private UserCurrentService userCurrentService;
    private RubricRepository rubricRepository;
    private AppConfig appConfig;

    @Autowired
    public RubricController(UserCurrentService userCurrentService,
                            RubricRepository rubricRepository,
                            AppConfig appConfig) {
        this.userCurrentService = userCurrentService;
        this.rubricRepository = rubricRepository;
        this.appConfig = appConfig;
    }

    @GetMapping("/administration/rubrics")
    public String viewRubrics(Principal principal, Model model) {

        User user = userCurrentService.getCurrentUser(principal);

        model.addAttribute("rubrics", rubricRepository.findAll());
        model.addAttribute("rubricSelect", new Rubric());

        model.addAttribute("currentPage", "adminRubrics");
        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
        model.addAttribute("user", user);
        return "rubrics";
    }

    @PostMapping("/administration/rubrics")
    public String updateRubric(Rubric rubricSelect) {

        rubricSelect.setDateStamp(ZonedDateTime.now(ZoneId.of("UTC")));
        rubricRepository.updateRubric(rubricSelect);
        return "redirect:/rubrics";
    }

    @GetMapping("/administration/rubrics/{id}")
    public String newsRubricById(Principal principal,
                                 @PathVariable("id") Long id,
                                 Model model) {

        User user = userCurrentService.getCurrentUser(principal);

        model.addAttribute("rubrics", rubricRepository.findAll());
        model.addAttribute("rubricSelect", rubricRepository.findById(id));

        model.addAttribute("currentPage", "adminRubrics");
        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
        model.addAttribute("user", user);
        return "rubrics";
    }

    @PostMapping("/administration/rubrics/{id}")
    public String updateRubric1(Rubric rubricSelect) {

        rubricSelect.setDateStamp(ZonedDateTime.now(ZoneId.of("UTC")));
        rubricRepository.updateRubric(rubricSelect);
        return "redirect:/administration/rubrics/{id}";
    }
}
