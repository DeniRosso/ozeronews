package com.example.ozeronews.controllers;

import com.example.ozeronews.models.Rubric;
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

    @Autowired
    public RubricController(UserCurrentService userCurrentService,
                            RubricRepository rubricRepository) {
        this.userCurrentService = userCurrentService;
        this.rubricRepository = rubricRepository;
    }

    @GetMapping("/administration/rubrics")
    public String viewRubrics(Principal principal,
                              Model model) {
        model.addAttribute("rubrics", rubricRepository.findAll());
        model.addAttribute("rubricSelect", new Rubric());

        model.addAttribute("currentPage", "adminRubrics");
        model.addAttribute("user", userCurrentService.getCurrentUser(principal));
        return "rubrics";
    }

    @PostMapping("/administration/rubrics")
    public String updateRubric(Rubric rubricSelect,
                               Model model) {
        rubricSelect.setDateStamp(ZonedDateTime.now(ZoneId.of("UTC")));
        rubricRepository.updateRubric(rubricSelect);
        return "redirect:/rubrics";
    }

    @GetMapping("/administration/rubrics/{id}")
    public String newsRubricById(Principal principal,
                                 @PathVariable("id") Long id,
                                 Model model) {
        model.addAttribute("rubrics", rubricRepository.findAll());
        model.addAttribute("rubricSelect", rubricRepository.findById(id));

        model.addAttribute("currentPage", "adminRubrics");
        model.addAttribute("user", userCurrentService.getCurrentUser(principal));
        return "rubrics";
    }

    @PostMapping("/administration/rubrics/{id}")
    public String updateRubric1(Rubric rubricSelect,
                                Model model) {
        rubricSelect.setDateStamp(ZonedDateTime.now(ZoneId.of("UTC")));
        rubricRepository.updateRubric(rubricSelect);
        return "redirect:/administration/rubrics/{id}";
    }
}
