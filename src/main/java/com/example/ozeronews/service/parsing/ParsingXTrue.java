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
public class ParsingXTrue {

    @Autowired
    private ArticleSaveService articleSaveService;

    @Autowired
    private ArticleRepository articleRepository;

    @Value("${article.collection.count}")
    private int articleCollectionCount;

    public void getArticles() {
        String newsResourceKey = "xtrue";
        String newsResourceLink = "https://x-true.info";
        String newsLink = "https://x-true.info/rss.xml";
        String articleTitle;
        String articleLink;
        String articleNumber;
        String articleImage;
        String rubricAliasName;
        ZonedDateTime articleDatePublication;
        ZonedDateTime dateStamp;

        try {
            URL feedSource = new URL(newsLink);
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

                articleNumber = articleLink.substring(20);
                articleNumber = "xtrue_" + articleNumber.substring(0, articleNumber.indexOf("-"));

                if (articleRepository.checkByArticleNumber(articleNumber)) break;

                articleImage = feed.getEntries().get(i).getDescription().getValue();
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
                        String categoryNames = category.getName();
                        while (categoryNames.indexOf(" / ") > 0) {
                            rubricAliasName = categoryNames.substring(0, categoryNames.indexOf(" / "));
                            articleRubricList.add(k++, new ArticleRubric().addRubricName(rubricAliasName, true, dateStamp));
                            categoryNames = categoryNames.substring(categoryNames.indexOf(" / ") + 3);
                        }
                        rubricAliasName = categoryNames;
                        if (rubricAliasName.length() >= 45) rubricAliasName = rubricAliasName.substring(0 ,44);
                        articleRubricList.add(k++, new ArticleRubric().addRubricName(rubricAliasName, true, dateStamp));
                    }
                }
                NewsResource newsResource = new NewsResource();
                newsResource.setResourceKey(newsResourceKey);
                newsResource.setResourceLink(newsResourceLink);
                newsResource.setNewsLink(newsLink);
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
            }
        } catch (IOException | FeedException e) {
            e.printStackTrace();
        }
    }
}
