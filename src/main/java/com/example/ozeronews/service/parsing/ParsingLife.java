package com.example.ozeronews.service.parsing;

import com.example.ozeronews.models.Article;
import com.example.ozeronews.models.ArticleRubric;
import com.example.ozeronews.models.NewsResource;
import com.example.ozeronews.repo.ArticleRepository;
import com.example.ozeronews.service.ArticleSaveService;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class ParsingLife {

    @Autowired
    private ArticleSaveService articleSaveService;

    @Autowired
    private ArticleRepository articleRepository;

    @Value("${article.collection.count}")
    private int articleCollectionCount;

    public int getArticles() {
        int articleCount = 0;
        String resourceKey = "life";
        String resourceFullName = "Life";
        String resourceShortName = "Life";
        String resourceLink = "https://life.ru";
        String resourceNewsLink = "https://life.ru/rss";

        String articleTitle;
        String articleLink;
        String articleNumber;
        String articleImage = null;
        String rubricAliasName;
        ZonedDateTime articleDatePublication;
        ZonedDateTime dateStamp;

        try {
            URL feedSource = new URL(resourceNewsLink);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));

            for (int i = 0; i < articleCollectionCount; i++) {

                articleTitle = null;
                articleLink = null;
                articleNumber = null;
                articleImage = null;
                rubricAliasName = null;
                articleDatePublication = null;
                dateStamp = null;

                articleTitle = feed.getEntries().get(i).getTitle();
                articleLink = feed.getEntries().get(i).getLink();
                articleNumber = resourceKey + "_" + articleLink.substring(18);

                if (articleRepository.checkByArticleNumber(articleNumber)) break;

                List<SyndEnclosure> enclosures = feed.getEntries().get(i).getEnclosures();
                if(enclosures!=null) {
                    for(SyndEnclosure enclosure : enclosures) {
                        if(enclosure.getType()!=null && enclosure.getType().equals("image/jpeg")){
                            articleImage = enclosure.getUrl();
                        }
                    }
                }

                System.out.println("## = \n" + feed.getEntries().get(i).getUri());
                System.out.println("## = \n" + feed.getEntries().get(i));

//                articleDatePublication = ZonedDateTime.ofInstant(
//                        Instant.parse(feed.getEntries().get(i).getPublishedDate().toInstant().toString()),
//                        ZoneId.of("UTC"));

//                String pubDate = feed.getEntries().get(i).getPublishedDate().toInstant().toString();
//
//                String date;
//                String time;
//
//                date = pubDate.substring(0, pubDate.indexOf("T"));
//                time = pubDate.substring(pubDate.indexOf("T") + 1);
//
//                articleDatePublication = ZonedDateTime.of(LocalDateTime.parse(
//                        (date + " " + time).substring(0, 19),
//                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
//                        ZoneId.of("UTC");

//                System.out.println("articleDatePublication = " + feed.getEntries().get(i).getUri());
//
//                articleDatePublication = ZonedDateTime.of(LocalDateTime.parse(
//                        (feed.getEntries().get(i).getPublishedDate().toInstant().toString()).substring(0, 19),
//                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
//                        ZoneId.of("UTC"));

                dateStamp = ZonedDateTime.now(ZoneId.of("UTC"));

                int k = 0;
                List<ArticleRubric> articleRubricList = new ArrayList<>();
                List<SyndCategory> categories = feed.getEntries().get(i).getCategories();
                if(categories!=null) {
                    for(SyndCategory category : categories) {

                        rubricAliasName = category.getName().substring(0, 1).replace(
                                category.getName().substring(0, 1),
                                category.getName().substring(0, 1).toUpperCase()) +
                                category.getName().substring(1);
                        if (rubricAliasName.length() >= 45) rubricAliasName = rubricAliasName.substring(0 ,44);
                        articleRubricList.add(k++, new ArticleRubric().addRubricName(rubricAliasName, true, dateStamp));
                    }
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

//                articleSaveService.saveArticle(article);
                articleCount++;

                System.out.println("*************************");
//                System.out.println("article " + resourceKey + " = " + article);
                System.out.println("articleTitle = " + articleTitle);
                System.out.println("articleLink = " + articleLink);
                System.out.println("articleNumber = " + articleNumber);
                System.out.println("articleImage = " + articleImage);
                System.out.println("articleRubricList = " + articleRubricList);
                System.out.println("articleDatePublication = " + articleDatePublication);
                System.out.println("dateStamp = " + dateStamp);
                System.out.println("*************************");
            }
        } catch (IOException | FeedException e) {
            e.printStackTrace();
        }
        return articleCount;
    }
}
