package com.example.ozeronews.controllers;

import com.example.ozeronews.models.Article;
import com.example.ozeronews.models.User;
import com.example.ozeronews.repo.*;
import com.example.ozeronews.service.ArticleFindService;
import com.example.ozeronews.service.UserCurrentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("")
public class NewsController {

    private ArticleFindService articleFindService;
    private ArticleRepository articleRepository;
    private SubscriptionRepository subscriptionRepository;
    private RubricRepository rubricRepository;
    private NewsResourceRepository newsResourceRepository;
    private UserRepository userRepository;
    private UserCurrentService userCurrentService;

    @Autowired
    public NewsController(ArticleFindService articleFindService,
                          ArticleRepository articleRepository,
                          SubscriptionRepository subscriptionRepository,
                          RubricRepository rubricRepository,
                          NewsResourceRepository newsResourceRepository,
                          UserRepository userRepository,
                          UserCurrentService userCurrentService) {
        this.articleFindService = articleFindService;
        this.articleRepository = articleRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.rubricRepository = rubricRepository;
        this.newsResourceRepository = newsResourceRepository;
        this.userRepository = userRepository;
        this.userCurrentService = userCurrentService;
    }

//    <div sec:authorize="isAuthenticated()" class="row">
//        <span sec:authentication="name">Username</span>
//        <span>[[${#request.userPrincipal.principal.name}]]</span>
//        <span>[[${#request.userPrincipal.principal.email}]]</span>
//    </div>


    // Загрузка дополнительных статей
    @GetMapping("/news")
    @ResponseBody
    public Iterable<Article> loadNewsArticles(Principal principal,
            @RequestParam(value="currentPage") String currentPage,
            @RequestParam(value="section") String section,
            @RequestParam(value="name") String name,
            @PageableDefault(sort = { "date_publication" }, direction = Sort.Direction.DESC) Pageable pageable) {

        Iterable<Article> articles = null;
        int startNumberNews;
        startNumberNews = (pageable.getPageNumber()) * pageable.getPageSize();

        if (currentPage.equals("news")) {
            switch (section) {
                case "news":
                    articles = articleFindService.findAllArticlesByLimit("UTC", startNumberNews, pageable.getPageSize());
                    //Page<Article> articlePage = articleRepo.findAll(pageable);
                    break;
                case "rubrics":
                    articles = articleFindService.findArticlesByRubric("UTC", name, startNumberNews, pageable.getPageSize());
                    break;
                case "resources":
                    articles = articleFindService.findArticlesByResource("UTC", name, startNumberNews, pageable.getPageSize());
                    break;
            }
        }

        if (currentPage.equals("subscriptions")) {
            switch (section) {
                case "news":
                    articles = articleFindService.findArticlesBySubscriptions("UTC", principal.getName(), startNumberNews, pageable.getPageSize());
                    break;
                case "rubrics":
                    articles = articleFindService.findArticlesByRubric("UTC", name, startNumberNews, pageable.getPageSize());
                    break;
                case "resources":
                    articles = articleFindService.findArticlesByResource("UTC", name, startNumberNews, pageable.getPageSize());
                    break;
            }
        }
        return articles;
    }

    // Поиск статей
    @GetMapping("/news/id")
    @ResponseBody
    public Iterable<Article> loadArticleById(Principal principal,
            @RequestParam(value="currentPage") String currentPage,
            @RequestParam(value="id") String articleId,
            @PageableDefault(sort = { "date_publication" }, direction = Sort.Direction.DESC) Pageable pageable) {
        Iterable<Article> articles = null;
        int startNumberNews;
        startNumberNews = (pageable.getPageNumber()) * pageable.getPageSize();

        if (articleId == null && articleId.isEmpty()) {
            System.out.println("Индификатора статьи нет");
            return null;
        }

        articles = articleRepository.findArticleById(Long.valueOf(articleId), startNumberNews, pageable.getPageSize());

        return articles;
    }


    // Поиск статей
    @GetMapping("/news/search")
    @ResponseBody
    public Iterable<Article> loadSearchArticles(Principal principal,
            @RequestParam(value="currentPage") String currentPage,
            @RequestParam(value="section") String section,
            @RequestParam(value="name") String name,
            @RequestParam(value="search") String search,
            @PageableDefault(sort = { "date_publication" }, direction = Sort.Direction.DESC) Pageable pageable) {

        Iterable<Article> articles = null;
        int startNumberNews;
        startNumberNews = (pageable.getPageNumber()) * pageable.getPageSize();

//        User user = userCurrentService.getCurrentUser(principal);

        if (search == null && search.isEmpty()) {
            System.out.println("Не значения для поиска статей");
            return null;
        }

        if (currentPage.equals("news")) {
            switch (section) {
                case "news":
                    articles = articleFindService.findArticlesBySearch("UTC", search, startNumberNews, pageable.getPageSize());
                    break;
                case "rubrics":
                    articles = articleFindService.findArticlesBySearch("UTC", search, startNumberNews, pageable.getPageSize());
                    break;
                case "resources":
                    articles = articleFindService.findArticlesBySearch("UTC", search, startNumberNews, pageable.getPageSize());
                    break;
            }
        }

        if (currentPage.equals("subscriptions")) {
            switch (section) {
                case "news":
                    articles = articleFindService.findSubscriptionsArticlesBySearch("UTC", search, principal.getName(), startNumberNews, pageable.getPageSize());
                    break;
                case "rubrics":
                    articles = articleFindService.findSubscriptionsArticlesBySearch("UTC", search, principal.getName(), startNumberNews, pageable.getPageSize());
                    break;
                case "resources":
                    articles = articleFindService.findSubscriptionsArticlesBySearch("UTC", search, principal.getName(), startNumberNews, pageable.getPageSize());
                    break;
            }
        }
        return articles;
    }

    @GetMapping("/news/{section}/{name}")
    public String viewNews(Principal principal,
                           @PathVariable("section") String section,
                           @PathVariable("name") String name,
                           @PageableDefault(sort = { "date_publication" }, direction = Sort.Direction.DESC) Pageable pageable,
                           Model model) {

        int countResources = 10;
        int countRubrics = 10;
        int[] countArticlesList = {10, 30, 50, 100};

        User user = userCurrentService.getCurrentUser(principal);

        model.addAttribute("section", section);
        model.addAttribute("name", name);
        model.addAttribute("countArticlesList", countArticlesList);
        model.addAttribute("pageable", pageable);
        model.addAttribute("currentPage", "news");
        model.addAttribute("articles", "not null");
        model.addAttribute("topRubrics", rubricRepository.findTopRubrics(countRubrics));
        model.addAttribute("allRubrics", rubricRepository.findAllRubrics());
        model.addAttribute("topNewsResources", newsResourceRepository.findTopResources(countResources));
        model.addAttribute("allNewsResources", newsResourceRepository.findAllResources());
        model.addAttribute("search", null);

        model.addAttribute("user", user);

        return "news";
    }

    @GetMapping("/subscriptions/{section}/{name}")
    public String viewSubscription(Principal principal,
                                   @PathVariable("section") String section,
                                   @PathVariable("name") String name,
                                   @PageableDefault(sort = { "date_publication" }, direction = Sort.Direction.DESC) Pageable pageable,
                                   Model model) {

        int countRubrics = 10;
        int countResources = 10;
        int[] countArticlesList = {10, 30, 50, 100};

        User user = userCurrentService.getCurrentUser(principal);

        model.addAttribute("section", section);
        model.addAttribute("name", name);
        model.addAttribute("countArticlesList", countArticlesList);
        model.addAttribute("pageable", pageable);
        model.addAttribute("currentPage", "subscriptions");
        model.addAttribute("articles", subscriptionRepository.checkByEmail(user) ? "not null" : null);
        model.addAttribute("topRubrics", rubricRepository.findTopRubrics(countRubrics));
        model.addAttribute("allRubrics", rubricRepository.findAllRubrics());
        model.addAttribute("topNewsResources", newsResourceRepository.findTopResources(countResources));
        model.addAttribute("allNewsResources", newsResourceRepository.findAllResources());
        model.addAttribute("search", null);

        model.addAttribute("user", user);
        return "news";
    }

//    @PostMapping("/{section}/{name}/search")
//    public String search(Principal principal,
//                         @PathVariable("section") String section,
//                         @PathVariable("name") String name,
//                         @PageableDefault(sort = { "date_publication" }, direction = Sort.Direction.DESC) Pageable pageable,
//                         @RequestParam String search,
//                         Model model) {
//
//        // Получить значение из поля поиска
//        // Сделать запрос в БД и найти по статьи по критерию
//        // Передать статьи на страницу
//        Iterable<Article> articles = null;
//        int startNumberNews;
//        int countRubrics = 10;
//        int countResources = 10;
//        int[] countArticlesList = {10, 30, 50, 100};
//        startNumberNews = (pageable.getPageNumber()) * pageable.getPageSize();
//
//
//        if (search != null && !search.isEmpty()) {
//            // Выполнить поиск по критерию search
//            articles = articleFindService.findArticlesBySearch("UTC", search, startNumberNews, pageable.getPageSize());
//        } else {
//            // Не выполнять поиск
//        }
//        model.addAttribute("section", section);
//        model.addAttribute("name", name);
//        model.addAttribute("countArticlesList", countArticlesList);
//        model.addAttribute("pageable", pageable);
//        model.addAttribute("articles", articles);
//        model.addAttribute("currentPage", "search");
//        model.addAttribute("topRubrics", rubricRepository.findTopRubrics(countRubrics));
//        model.addAttribute("allRubrics", rubricRepository.findAllRubrics());
//        model.addAttribute("topNewsResources", newsResourceRepository.findTopResources(countResources));
//        model.addAttribute("allNewsResources", newsResourceRepository.findAllResources());
//        model.addAttribute("search", search);
//
//        if (articles == null) {
//            model.addAttribute("messageType", "alert alert-danger");
//            model.addAttribute("message", "По запросу '" + search + "' не найдено новостей.");
//        } else {
//            model.addAttribute("messageType", "alert alert-success");
//            model.addAttribute("message", "По запросу '" + search + "' " + "articles.size()" + " новость(и).");
//        }
//
//        model.addAttribute("user", userCurrentService.getCurrentUser(principal));
//
//        return "news";
//    }
}
