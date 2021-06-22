package com.example.ozeronews.controllers;

import com.example.ozeronews.models.CategoryResource;
import com.example.ozeronews.models.NewsResource;
import com.example.ozeronews.repo.CategoryResourceRepository;
import com.example.ozeronews.repo.NewsResourceRepository;
import com.example.ozeronews.service.CategoryResourceService;
import com.example.ozeronews.service.UserCurrentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Controller
@RequestMapping("")
public class CategoryResourceController {

    private UserCurrentService userCurrentService;
    private NewsResourceRepository newsResourceRepository;
    private CategoryResourceRepository categoryResourceRepository;
    private CategoryResourceService categoryResourceService;

    @Autowired
    public CategoryResourceController(UserCurrentService userCurrentService,
                                      NewsResourceRepository newsResourceRepository,
                                      CategoryResourceRepository categoryResourceRepository,
                                      CategoryResourceService categoryResourceService) {
        this.userCurrentService = userCurrentService;
        this.newsResourceRepository = newsResourceRepository;
        this.categoryResourceRepository = categoryResourceRepository;
        this.categoryResourceService = categoryResourceService;
    }

    @GetMapping("/administration/categoriesresources")
    public String viewCategoryResource(Principal principal,
                                       CategoryResource categoryResource,
                                       Model model) {

        model.addAttribute("categoryResource", new CategoryResource());
        model.addAttribute("categoryResourceSelect", new CategoryResource());
        model.addAttribute("categoriesResources", categoryResourceRepository.findAll());
        model.addAttribute("resources", new NewsResource());
        model.addAttribute("currentPage", "adminCategoriesResources");
        model.addAttribute("user", userCurrentService.getCurrentUser(principal));
        return "categoriesresources";
    }

    @PostMapping("/administration/categoriesresources/new")
    public String newCategoryResource(CategoryResource categoryResource) {

        if (categoryResource.getId() != null) {

            categoryResource.setDateStamp(ZonedDateTime.now(ZoneId.of("UTC")));

            System.out.println("categoryResource (update) = " + categoryResource);
            categoryResourceRepository.update(categoryResource);
            return "redirect:/administration/categoriesresources";
        }
        categoryResource.setActive(true);
        categoryResource.setDateStamp(ZonedDateTime.now(ZoneId.of("UTC")));
        categoryResourceRepository.save(categoryResource);

        return "redirect:/administration/categoriesresources";
    }

    @GetMapping("/administration/categoriesresources/{id}")
    @ResponseBody
    public Iterable<NewsResource> choiceCategoryResource(
            @PathVariable("id") Long id,
            @RequestParam(value="categoryResourceId") Long categoryResourceId) {
        Iterable<NewsResource> resources =
                categoryResourceService.filterCategoryResource(
                        categoryResourceRepository.findById(categoryResourceId));
        return resources;
    }

    @GetMapping("/administration/categoriesresources/category/{id}")
    @ResponseBody
    public CategoryResource loadCategoryResource(@PathVariable("id") Long id) {
        CategoryResource categoryResource = categoryResourceRepository.findById(id);
        return categoryResource;
    }



//    @GetMapping("/administration/categoriesresources/{id}")
//    public String choiceCategoryResource(Principal principal,
//                                         @PathVariable("id") Long id,
//                                         Model model) {
//        CategoryResource categoryResource = new CategoryResource();
//        for (CategoryResource categoryResource1 : categoryResourceRepository.findById(id)) {
//            categoryResource = categoryResource1;
//        }
//
//        NewsResource newsResource = new NewsResource();
////        newsResource.getResourceToCategories().iterator().next().isActive()
////        newsResource.getResourceToCategories().stream().iterator().next().isActive()
//
//        model.addAttribute("categoryResource", categoryResource);
//        model.addAttribute("categoryResourceSelect", categoryResource);
//        model.addAttribute("categoriesResources", categoryResourceRepository.findAll());
//        model.addAttribute("resources", categoryResourceService.filterCategoryResource(categoryResource));
//        model.addAttribute("currentPage", "adminCategoriesResources");
//        model.addAttribute("user", userCurrentService.getCurrentUser(principal));
//        return "categoriesresources";
//    }

//    http://localhost:8080/administration/categoriesresources/resource?categoryResourceId=1&resourceId=16

    @GetMapping("/administration/categoriesresources/resource")
    @ResponseBody
    public Iterable<NewsResource> createCategoryResource(
                 @RequestParam(value="categoryResourceId") Long categoryResourceId,
                 @RequestParam(value="resourceId") Long resourceId) {

        categoryResourceService.createResourceToCategory(
                newsResourceRepository.findById(resourceId),
                categoryResourceRepository.findById(categoryResourceId));
        Iterable<NewsResource> resources =
                categoryResourceService.filterCategoryResource(
                        categoryResourceRepository.findById(categoryResourceId));
        return resources;
    }
}
