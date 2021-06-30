package com.example.ozeronews.service.parsing;

import com.example.ozeronews.models.Article;
import com.example.ozeronews.models.NewsResource;
import com.example.ozeronews.repo.ArticleRepository;
import com.example.ozeronews.service.ArticleSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;

@Component
public class ParsingMyShowsMe {

    @Autowired
    private ArticleSaveService articleSaveService;

    @Autowired
    private ArticleRepository articleRepository;

    @Value("${article.collection.count}")
    private int articleCollectionCount;

    @Autowired
    final RestTemplate restTemplate = new RestTemplate();

    public int getArticle() {
        int articleCount = 0;
        String newsResourceKey = "kmru";
        String newsResourceLink = "https://www.km.ru/";
        String newsLink = "https://www.km.ru/xml/rss/main";
        String articleTitle;
        String articleLink;
        String articleNumber;
        String articleImage;
        String rubricAliasName;
        ZonedDateTime articleDatePublication;
        ZonedDateTime dateStamp;


//        newsLink = "http://jsonplaceholder.typicode.com/posts?_limit=10";
        newsLink = "https://api.myshows.me/shows/top/all/";
        newsLink = "https://api.myshows.me/shows/top/all/";

        final String stringPosts = restTemplate.getForObject(newsLink, String.class);
        System.out.println(stringPosts);



//        RestTemplate restTemplate = new RestTemplate();
//
//        String url = "endpoint url";
//        String requestJson = "{\"queriedQuestion\":\"Is there pain in your hand?\"}";
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
//        String answer = restTemplate.postForObject(url, entity, String.class);
//        System.out.println(answer);




        NewsResource newsResource = new NewsResource();
//        newsResource.setResourceKey(newsResourceKey);
//        newsResource.setResourceLink(newsResourceLink);
//        newsResource.setNewsLink(newsLink);
//        newsResource.setActive(true);
//        newsResource.setDateStamp(dateStamp);

        Article article = new Article();
//        article.setResourceId(newsResource);
//        article.setArticleRubric(articleRubricList);
//        article.setNumber(articleNumber);
//        article.setTitle(articleTitle);
//        article.setLink(articleLink);
//        article.setImage(articleImage);
//        article.setDatePublication(articleDatePublication);
//        article.setDateStamp(dateStamp);

//        articleSaveService.saveArticle(article);
        articleCount++;

//        System.out.println("*************************");
////                System.out.println("article " + newsResourceKey + " = " + article);
//        System.out.println("articleTitle = " + articleTitle);
//        System.out.println("articleLink = " + articleLink);
//        System.out.println("articleNumber = " + articleNumber);
//        System.out.println("articleImage = " + articleImage);
//        System.out.println("articleRubricList = " + articleRubricList);
//        System.out.println("articleDatePublication = " + articleDatePublication);
//        System.out.println("dateStamp = " + dateStamp);
//        System.out.println("*************************");

        return articleCount;
    }
}
