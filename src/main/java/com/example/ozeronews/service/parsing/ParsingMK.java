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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
public class ParsingMK {

    @Autowired
    private ArticleSaveService articleSaveService;

    @Autowired
    private ArticleRepository articleRepository;

    @Value("${article.collection.count}")
    private int articleCollectionCount;

    public void getArticles() {
        String newsResourceKey = "mk";
        String newsResourceLink = "https://www.mk.ru/";
        String newsLink = "https://www.mk.ru/rss/news/index.xml";
        String articleTitle = null;
        String articleLink;
        String articleNumber = null;
        String articleImage = null;
        String rubricAliasName;
        ZonedDateTime articleDatePublication = null;
        ZonedDateTime dateStamp = null;

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
//                articleNumber = newsResourceKey + "_" + articleLink.substring(
//                        articleLink.lastIndexOf("/") + 1, articleLink.indexOf("-"));

                List<SyndEnclosure> enclosures = feed.getEntries().get(i).getEnclosures();
                if(enclosures!=null) {
                    for(SyndEnclosure enclosure : enclosures) {
                        if(enclosure.getType()!=null && enclosure.getType().equals("image/jpeg")){
                            articleImage = enclosure.getUrl();
                        }
                    }
                }

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

                Document docArticleDescription = Jsoup.connect(articleLink)
                        .userAgent("Safari")
                        .get();

//                <script type="application/json" class="js-mk-item-settings">{"type":"article","id":3066532}</script>

                Elements articlesDescriptions = docArticleDescription.getElementsByTag("main");
                for (Element articleDescription : articlesDescriptions) {
                    articleNumber = articleDescription.select("script[class=js-mk-item-settings]").toString();
                    articleNumber = newsResourceKey + "_" + articleNumber.substring(
                            articleNumber.indexOf("\"id\":") + 5, articleNumber.lastIndexOf("}"));
                }
                if (articleRepository.checkByArticleNumber(articleNumber)) break;

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

//                System.out.println("*************************");
////                System.out.println("article (RIA) = " + article);
//                System.out.println("articleTitle = " + articleTitle);
//                System.out.println("articleLink = " + articleLink);
//                System.out.println("articleNumber = " + articleNumber);
//                System.out.println("articleImage = " + articleImage);
//                System.out.println("articleRubricList = " + articleRubricList);
//                System.out.println("articleDatePublication = " + articleDatePublication);
//                System.out.println("dateStamp = " + dateStamp);
//                System.out.println("*************************");
            }
        } catch (IOException | FeedException e) {
            e.printStackTrace();
        }
    }
}
