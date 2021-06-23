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
public class ParsingNovayaGazeta {

    @Autowired
    private ArticleSaveService articleSaveService;

    @Autowired
    private ArticleRepository articleRepository;

    @Value("${article.collection.count}")
    private int articleCollectionCount;

    public int getArticles() {
        int articleCount = 0;
        String newsResourceKey = "novayagazeta";
        String newsResourceLink = "https://novayagazeta.ru/";
        String newsLink = "https://novayagazeta.ru/feed/rss";
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

                articleTitle = feed.getEntries().get(i).getTitle().trim();
                articleLink = feed.getEntries().get(i).getLink();

                articleNumber = newsResourceKey + "_" + articleLink.substring(articleLink.lastIndexOf("/") + 1);
                articleNumber = (articleNumber.length() >= 45 ? articleNumber.substring(0, 45) : articleNumber);

                if (articleRepository.checkByArticleNumber(articleNumber)) break;

                List<SyndEnclosure> enclosures = feed.getEntries().get(i).getEnclosures();
                if(enclosures != null) {
                    for(SyndEnclosure enclosure : enclosures) {
                        if(enclosure.getType() != null &&
                                (enclosure.getType().equals("image/jpeg") || enclosure.getType().equals("image/gif"))) {
                            articleImage = enclosure.getUrl();
                        }
                    }
                }

//                articleDatePublication = ZonedDateTime.ofInstant(
//                        Instant.parse(feed.getEntries().get(i).getPublishedDate().toInstant().toString()),
//                        ZoneId.of("UTC"));

                dateStamp = ZonedDateTime.now(ZoneId.of("UTC"));

//                int k = 0;
//                List<ArticleRubric> articleRubricList = new ArrayList<>();
//                List<SyndCategory> categories = feed.getEntries().get(i).getCategories();
//                if(categories!=null) {
//                    for(SyndCategory category : categories) {
//                        rubricAliasName = category.getName();
//                        if (rubricAliasName.length() >= 45) rubricAliasName = rubricAliasName.substring(0 ,44);
//                        articleRubricList.add(k++, new ArticleRubric().addRubricName(rubricAliasName, true, dateStamp));
//                    }
//                }

//<div class="components_container__2KInn" id="mainContainer">

//                <span class="Time_root__2b1Eg"><span>15:52, 23 июня 2021</span></span>

//                <span class="Genre_root__33t_y Genre_rubric__T_XJ8">
//                    <span class="Genre_genre__iUUbY">Интервью</span>
//                    <span class="Genre_rubric__T_XJ8"> · Общество</span>
//                </span>

//                <img class="SingleImage_image__3qIDn" src="https://novayagazeta.ru/static/records/dc817fe9751c4869bea9fe02403c0592.webp" alt="">

                // Получение дополнительной информаций по статье
                Document docArticleDescription = Jsoup.connect(articleLink)
                        .userAgent("Safari")
                        .get();

                String datePub = null;

//                if (!docArticleDescription.getElementById("mainContainer")
//                        .select("span[class^=Time_root__]").first()
//                        .select("span").first().text().isEmpty()) {
//                    datePub = docArticleDescription.getElementById("mainContainer")
//                            .select("span[class^=Time_root__]").first()
//                            .select("span").first().text();
//                }

                datePub = docArticleDescription.getElementById("mainContainer")
                        .select("span[class*=Time_root]").first()
                        .select("span").first().text();
                System.out.println("datePub = " + datePub);

                if (!docArticleDescription.getElementById("mainContainer")
                        .select("img[class=SingleImage_image__3qIDn]").isEmpty()) {
                    articleImage = docArticleDescription.getElementById("mainContainer")
                            .select("img[class=SingleImage_image__3qIDn]").first().attr("src");
                }

                int k = 0;
                List<ArticleRubric> articleRubricList = new ArrayList<>();
                if (!docArticleDescription.getElementById("mainContainer")
                        .select("span[class=Genre_root__]").isEmpty()) {
                    Elements categories = docArticleDescription.getElementById("mainContainer")
                            .select("span[class=Genre_root__]");
                    for(Element category : categories) {
                        rubricAliasName = category.text();
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

//                articleSaveService.saveArticle(article);
                articleCount++;

                System.out.println("*************************");
//                System.out.println("article " + newsResourceKey + " = " + article);
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
