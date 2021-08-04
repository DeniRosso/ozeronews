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
public class ParsingEurosport {

    @Autowired
    private ArticleSaveService articleSaveService;

    @Autowired
    private ArticleRepository articleRepository;

    @Value("${article.collection.count}")
    private int articleCollectionCount;

    public int getArticles() {
        int articleCount = 0;
        String resourceKey = "eurosport";
        String resourceFullName = "Eurosport";
        String resourceShortName = "Eurosport";
        String resourceLink = "https://www.eurosport.ru/";
        String resourceNewsLink = "https://www.eurosport.ru/rss.xml";

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

                articleTitle = feed.getEntries().get(i).getTitle().trim();
                articleLink = feed.getEntries().get(i).getLink();

                articleNumber = resourceKey + "_" + feed.getEntries().get(i).getUri().substring(
                        feed.getEntries().get(i).getUri().indexOf("eurosport-") + 10);
                articleNumber = (articleNumber.length() >= 45 ? articleNumber.substring(0, 45) : articleNumber);

                if (articleRepository.checkByArticleNumber(articleNumber)) break;

//                List<SyndEnclosure> enclosures = feed.getEntries().get(i).getEnclosures();
//                if(enclosures != null) {
//                    for(SyndEnclosure enclosure : enclosures) {
//                        if(enclosure.getType() != null &&
//                                (enclosure.getType().equals("image/jpeg") || enclosure.getType().equals("image/gif"))) {
//                            articleImage = enclosure.getUrl();
//                        }
//                    }
//                }

                articleImage = feed.getEntries().get(i).getDescription().getValue().substring(
                        feed.getEntries().get(i).getDescription().getValue().indexOf("src=\"") + 5,
                        feed.getEntries().get(i).getDescription().getValue().indexOf("\" ")
                );

                articleDatePublication = ZonedDateTime.ofInstant(
                        Instant.parse(feed.getEntries().get(i).getPublishedDate().toInstant().toString()),
                        ZoneId.of("UTC"));

                dateStamp = ZonedDateTime.now(ZoneId.of("UTC"));

                int k = 0;
                List<ArticleRubric> articleRubricList = new ArrayList<>();
                List<SyndCategory> categories = feed.getEntries().get(i).getCategories();
                if(categories!=null) {
                    for(SyndCategory category : categories) {
                        rubricAliasName = category.getName();
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
