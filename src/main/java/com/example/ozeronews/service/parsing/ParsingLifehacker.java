package com.example.ozeronews.service.parsing;

import com.example.ozeronews.models.Article;
import com.example.ozeronews.models.ArticleRubric;
import com.example.ozeronews.models.NewsResource;
import com.example.ozeronews.repo.ArticleRepository;
import com.example.ozeronews.service.ArticleSaveService;
import com.rometools.rome.feed.synd.SyndCategory;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ParsingLifehacker {

    @Autowired
    private ArticleSaveService articleSaveService;

    @Autowired
    private ArticleRepository articleRepository;

    @Value("${article.collection.count}")
    private int articleCollectionCount;

    public int getArticles() {
        int articleCount = 0;
        String resourceKey = "lifehacker";
        String resourceFullName = "Лайфхакер";
        String resourceShortName = "Лайфхакер";
        String resourceLink = "https://lifehacker.ru/";
        String resourceNewsLink = "https://lifehacker.ru/feed/";

        String articleTitle;
        String articleLink;
        String articleNumber;
        String articleImage;
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

//                articleNumber = resourceKey + "_" + feed.getEntries().get(i).getUri().substring(
//                        feed.getEntries().get(i).getUri().lastIndexOf("=") + 1);

                articleNumber = resourceKey + "_" + articleLink.substring(
                        "https://lifehacker.ru/".length(), articleLink.lastIndexOf("/"));
                if (articleNumber.length() > 45) articleNumber = articleNumber.substring(0, 45);

                if (articleRepository.checkByArticleNumber(articleNumber)) break;

//                List<SyndEnclosure> enclosures = feed.getEntries().get(i).getEnclosures();
//                if(enclosures!=null) {
//                    for(SyndEnclosure enclosure : enclosures) {
//                        if(enclosure.getType()!=null && enclosure.getType().equals("image/jpeg")){
//                            articleImage = enclosure.getUrl();
//                        }
//                    }
//                }

                articleImage = feed.getEntries().get(i).getDescription().toString();
                articleImage = articleImage.substring(articleImage.indexOf("src=\"") + 5);
                articleImage = articleImage.substring(0, articleImage.indexOf("\""));

                articleDatePublication = ZonedDateTime.ofInstant(
                        Instant.parse(feed.getEntries().get(i).getPublishedDate().toInstant().toString()),
                        ZoneId.of("UTC"));

                dateStamp = ZonedDateTime.now(ZoneId.of("UTC"));

                int k = 0;
                List<ArticleRubric> articleRubricList = new ArrayList<>();
                List<SyndCategory> categories = feed.getEntries().get(i).getCategories();
                if(categories!=null) {
                    for(SyndCategory category : categories) {
                        rubricAliasName = category.getName().substring(0, 1).toUpperCase() +
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

                articleSaveService.saveArticle(article);
                    articleCount++;
                }
        } catch (IOException | FeedException e) {
            e.printStackTrace();
        }
        return articleCount;
    }
}
