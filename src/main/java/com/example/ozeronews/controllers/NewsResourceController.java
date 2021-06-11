package com.example.ozeronews.controllers;

import com.example.ozeronews.models.NewsResource;
import com.example.ozeronews.repo.NewsResourceRepository;
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
public class NewsResourceController {

    private UserCurrentService userCurrentService;
    private NewsResourceRepository newsResourceRepository;

    @Autowired
    public NewsResourceController(UserCurrentService userCurrentService,
                                  NewsResourceRepository newsResourceRepository) {
        this.userCurrentService = userCurrentService;
        this.newsResourceRepository = newsResourceRepository;
    }

    @GetMapping("/administration/news-resources")
    public String viewNewsResources(Principal principal,
                                    Model model) {
        model.addAttribute("newsResources", newsResourceRepository.findAll());
        model.addAttribute("newsResourceSelect", new NewsResource());

        model.addAttribute("currentPage", "adminResources");
        model.addAttribute("user", userCurrentService.getCurrentUser(principal));
        return "news-resources";
    }

    @GetMapping("/administration/news-resources/{id}")
    public String ViewNewsResourceByKey(Principal principal,
                                        @PathVariable("id") Long id,
                                        Model model) {
        model.addAttribute("newsResources", newsResourceRepository.findAll());
        model.addAttribute("newsResourceSelect", newsResourceRepository.findById(id));

        model.addAttribute("currentPage", "adminResources");
        model.addAttribute("user", userCurrentService.getCurrentUser(principal));
        return "news-resources";
    }

    @PostMapping("/administration/news-resources/{id}")
    public String updateNewsResources(NewsResource newsResourceSelect,
                                      Model model) {
        newsResourceSelect.setDateStamp(ZonedDateTime.now(ZoneId.of("UTC")));
        newsResourceRepository.updateResource(newsResourceSelect);
        return "redirect:/administration/news-resources/{id}";
    }

}
