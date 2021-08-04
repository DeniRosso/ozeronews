package com.example.ozeronews.service.parsing;

import com.example.ozeronews.models.Article;
import com.example.ozeronews.models.ArticleRubric;
import com.example.ozeronews.models.NewsResource;
import com.example.ozeronews.repo.ArticleRepository;
import com.example.ozeronews.service.ArticleSaveService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class ParsingTsargrad {

    @Autowired
    private ArticleSaveService articleSaveService;

    @Autowired
    private ArticleRepository articleRepository;

    @Value("${article.collection.count}")
    private int articleCollectionCount;

    public int getArticles() {
        int articleCount = 0;
        String resourceKey = "tsargrad";
        String resourceFullName = "Царьград";
        String resourceShortName = "Царьград";
        String resourceLink = "https://tsargrad.tv";
        String resourceNewsLink = "https://tsargrad.tv/news";

        String articleTitle;
        String articleLink;
        String articleNumber;
        String articleImage;
        String rubricAliasName;
        ZonedDateTime articleDatePublication;
        ZonedDateTime dateStamp;

        try {
        // Полечение web страницы
            Document doc = Jsoup.connect(resourceNewsLink)
                    .userAgent("Safari") //userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();

        // Получение нужных статей
            Elements articles = doc.getElementsByTag("article");

        // Получение элементов по каждой статье
            for (int i = 0; i < articleCollectionCount; i++) {

                articleTitle = null;
                articleLink = null;
                articleNumber = null;
                articleImage = null;
                rubricAliasName = null;
                articleDatePublication = null;
                dateStamp = null;

                articleTitle = articles.get(i).select("a").first().text();
                articleLink = resourceLink + articles.get(i).select("a").first().attr("href");
                articleNumber = resourceKey + "_" + articleLink.substring(articleLink.length()-6);

                if (articleRepository.checkByArticleNumber(articleNumber)) break;

                articleDatePublication = ZonedDateTime.of(LocalDateTime.parse(
                        (LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd ")) +
                                articles.get(i).select("time").text() + ":01"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        ZoneId.of("Europe/Moscow")
                ).withZoneSameInstant(ZoneId.of("UTC"));

                dateStamp = ZonedDateTime.now(ZoneId.of("UTC"));

        // Получение дополнительной информаций по статье
                Document docArticleDescription = Jsoup.connect(articleLink)
                        .userAgent("Safari")
                        .get();

                Element articleDescription = docArticleDescription.getElementsByTag("article").first();

                articleImage = articleDescription.select("img").attr("src");

                int k = 0;
                List<ArticleRubric> articleRubricList = new ArrayList<>();
                for (int j = 0; j < articleDescription.select("a[class=news-item__category-name]").size(); j++) {
                    rubricAliasName = articleDescription.select("a[class=news-item__category-name]").get(j).text();
                    if (rubricAliasName.length() >= 45) rubricAliasName = rubricAliasName.substring(0 ,44);
                    articleRubricList.add(k++, new ArticleRubric().addRubricName(rubricAliasName, true, dateStamp));
                }
                NewsResource newsResource = new NewsResource();
                newsResource.setResourceKey(resourceKey);
                newsResource.setFullName(resourceFullName);
                newsResource.setShortName(resourceShortName);
                newsResource.setResourceLink(resourceLink);
                newsResource.setNewsLink(resourceNewsLink);
                newsResource.setActive(true);
                newsResource.setDateStamp(dateStamp);

                Article article = new Article();
                article.setResourceId(newsResource);
                article.setArticleRubric(articleRubricList);
                article.setNumber(articleNumber);
                article.setTitle(articleTitle);
                article.setLink(articleLink);
                article.setImage(articleImage);
                article.setDatePublication(articleDatePublication);
                article.setDateStamp(dateStamp);

                articleSaveService.saveArticle(article);
                articleCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return articleCount;
    }
}
