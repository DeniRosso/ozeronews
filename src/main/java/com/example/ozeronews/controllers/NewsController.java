package com.example.ozeronews.controllers;

import com.example.ozeronews.config.AppConfig;
import com.example.ozeronews.models.Article;
import com.example.ozeronews.models.Head;
import com.example.ozeronews.models.NewsResource;
import com.example.ozeronews.models.User;
import com.example.ozeronews.repo.*;
import com.example.ozeronews.service.ArticleFindService;
import com.example.ozeronews.service.CategoryResourceService;
import com.example.ozeronews.service.UserCurrentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("")
public class NewsController {

    private ArticleFindService articleFindService;
    private ArticleRepository articleRepository;
    private SubscriptionRepository subscriptionRepository;
    private RubricRepository rubricRepository;
    private NewsResourceRepository newsResourceRepository;
    private CategoryResourceRepository categoryResourceRepository;
    private CategoryResourceService categoryResourceService;
    private UserRepository userRepository;
    private UserCurrentService userCurrentService;
    private AppConfig appConfig;

    @Autowired
    public NewsController(ArticleFindService articleFindService,
                          ArticleRepository articleRepository,
                          SubscriptionRepository subscriptionRepository,
                          RubricRepository rubricRepository,
                          NewsResourceRepository newsResourceRepository,
                          CategoryResourceRepository categoryResourceRepository,
                          CategoryResourceService categoryResourceService,
                          UserRepository userRepository,
                          UserCurrentService userCurrentService,
                          AppConfig appConfig) {
        this.articleFindService = articleFindService;
        this.articleRepository = articleRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.rubricRepository = rubricRepository;
        this.newsResourceRepository = newsResourceRepository;
        this.categoryResourceRepository = categoryResourceRepository;
        this.categoryResourceService = categoryResourceService;
        this.userRepository = userRepository;
        this.userCurrentService = userCurrentService;
        this.appConfig = appConfig;
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
            @RequestParam(value="maxId") Long maxId,
            @PageableDefault(sort = { "date_publication" }, direction = Sort.Direction.DESC) Pageable pageable) {

        Iterable<Article> articles = null;
        int startNumberNews;
        startNumberNews = (pageable.getPageNumber()) * pageable.getPageSize();

        if (currentPage.equals("news")) {
            switch (section) {
                case "news":
                    articles = articleFindService.findAllArticlesByLimit("UTC", maxId, startNumberNews, pageable.getPageSize());
                    //Page<Article> articlePage = articleRepo.findAll(pageable);
                    break;
                case "rubrics":
                    articles = articleFindService.findArticlesByRubric(name, "UTC", maxId, startNumberNews, pageable.getPageSize());
                    break;
                case "resources":
                    articles = articleFindService.findArticlesByResource(name, "UTC", maxId, startNumberNews, pageable.getPageSize());
                    break;
            }
        }

        if (currentPage.equals("subscriptions")) {
            switch (section) {
                case "news":
                    articles = articleFindService.findArticlesBySubscriptions(principal.getName(), "UTC", maxId, startNumberNews, pageable.getPageSize());
                    break;
                case "rubrics":
                    articles = articleFindService.findArticlesByRubric(name, "UTC", maxId, startNumberNews, pageable.getPageSize());
                    break;
                case "resources":
                    articles = articleFindService.findArticlesByResource(name, "UTC", maxId, startNumberNews, pageable.getPageSize());
                    break;
            }
        }
        return articles;
    }

    // Получение максимального ID статей
    @GetMapping("/news/max-id")
    @ResponseBody
    public String loadMaxIdArticle() {
        return String.valueOf(articleRepository.findMaxId().getId());
    }

    // Поиск статей
    @GetMapping("/news/id")
    @ResponseBody
    public Iterable<Article> loadArticleById(Principal principal,
            @RequestParam(value="currentPage") String currentPage,
            @RequestParam(value="id") String articleId,
            @PageableDefault(sort = { "date_publication" }, direction = Sort.Direction.DESC) Pageable pageable) {

        int startNumberNews;
        startNumberNews = (pageable.getPageNumber()) * pageable.getPageSize();

        if (articleId == null && articleId.isEmpty()) {
            System.out.println("Индификатора статьи нет");
            return null;
        }

        Iterable<Article> articles = articleRepository.findArticleById(Long.valueOf(articleId), startNumberNews, pageable.getPageSize());

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
        model.addAttribute("resources", newsResourceRepository.findAllResources());
        model.addAttribute("categoriesResources", categoryResourceRepository.findByActive(true));
        model.addAttribute("search", null);

        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
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
        model.addAttribute("resources", newsResourceRepository.findAllResources());
        model.addAttribute("categoriesResources", categoryResourceRepository.findByActive(true));
        model.addAttribute("search", null);

        model.addAttribute("head", appConfig.getHead());
        model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
        model.addAttribute("user", user);
        return "news";
    }

    @GetMapping("/news/{section}/{name}/{id}")
    public String viewNewsById(Principal principal,
                           @PathVariable("section") String section,
                           @PathVariable("name") String name,
                           @PathVariable("id") String id,
                           @PageableDefault(sort = { "date_publication" }, direction = Sort.Direction.DESC) Pageable pageable,
                           Model model) {

        int countResources = 10;
        int countRubrics = 10;
        int[] countArticlesList = {10, 30, 50, 100};
        int startNumberNews;
        startNumberNews = (pageable.getPageNumber()) * pageable.getPageSize();

        User user = userCurrentService.getCurrentUser(principal);

        List<Article> articles = (List<Article>) articleRepository
                .findArticleById(Long.valueOf(id), startNumberNews, pageable.getPageSize());
        Head head = appConfig.getHead();
        head.setWebsiteURL(head.getWebsiteURL() + "news/news/all/" + id + "?id=" + id);
        head.setTitle(articles.get(0).getTitle());
        head.setDescription("\"" + articles.get(0).getResourceId().getFullName() + "\"" + " | " +
                articles.get(0).getDatePublication().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
        head.setImage(articles.get(0).getImage());

        model.addAttribute("section", section);
        model.addAttribute("name", name);
        model.addAttribute("countArticlesList", countArticlesList);
        model.addAttribute("pageable", pageable);
        model.addAttribute("currentPage", "news");
        model.addAttribute("articles", "not null");
        model.addAttribute("topRubrics", rubricRepository.findTopRubrics(countRubrics));
        model.addAttribute("allRubrics", rubricRepository.findAllRubrics());
        model.addAttribute("topNewsResources", newsResourceRepository.findTopResources(countResources));
        model.addAttribute("resources", newsResourceRepository.findAllResources());
        model.addAttribute("categoriesResources", categoryResourceRepository.findByActive(true));
        model.addAttribute("search", null);

        model.addAttribute("head", head);
        model.addAttribute("userPicture", userCurrentService.getUserPicture(user));
        model.addAttribute("user", user);

        return "news";
    }

    @GetMapping("/news/categoriesresources/{id}")
    @ResponseBody
    public Iterable<NewsResource> choiceCategoryResource(
                                    @PathVariable("id") Long id,
                                    @RequestParam(value="categoryResourceId") Long categoryResourceId) {

        Iterable<NewsResource> resources =
                categoryResourceService.filterCategoryResourceById(
                        categoryResourceRepository.findById(categoryResourceId));
        return resources;
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
