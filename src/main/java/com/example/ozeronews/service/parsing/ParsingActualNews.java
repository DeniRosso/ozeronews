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
public class ParsingActualNews {

    @Autowired
    private ArticleSaveService articleSaveService;

    @Autowired
    private ArticleRepository articleRepository;

    @Value("${article.collection.count}")
    private int articleCollectionCount;

    public int getArticles() {
        int articleCount = 0;
        String newsResourceKey = "actualnews";
        String newsResourceLink = "https://actualnews.org/";
        String newsLink = "https://actualnews.org/";
        String articleTitle;
        String articleLink;
        String articleNumber;
        String articleImage;
        String rubricAliasName;
        ZonedDateTime articleDatePublication;
        ZonedDateTime dateStamp;

        try {
            // Полечение web страницы
            Document doc = Jsoup.connect(newsLink)
                    .userAgent("Safari") //userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();

            // Получение нужных статей
            Elements articles = doc.getElementsByClass("box").select("div[class=item]").select("a");

            // Получение элементов по каждой статье
            for (int i = 0; i < articleCollectionCount; i++) {

                articleTitle = null;
                articleLink = null;
                articleNumber = null;
                articleImage = null;
                rubricAliasName = null;
                articleDatePublication = null;
                dateStamp = null;

                articleLink = articles.get(i).attr("href");
                articleNumber = newsResourceKey + "_" + articleLink.substring(
                        articleLink.lastIndexOf("/") + 1, articleLink.indexOf("-"));

                if (articleRepository.checkByArticleNumber(articleNumber)) break;

                // Получение дополнительной информаций по статье
                Document docArticleDescription = Jsoup.connect(articleLink)
                        .userAgent("Safari")
                        .get();

                Elements articlesDescriptions = docArticleDescription.getElementsByTag("article");

                for (Element articleDescription : articlesDescriptions) {

                    articleTitle = articleDescription.select("span[id=news-title]").first().text();
                    articleImage = articleDescription.select("meta[itemprop=image]").first().attr("content");

//                    articleDatePublication = ZonedDateTime.ofInstant(
//                            Instant.parse(articleDescription.select("meta[itemprop=datePublished]").first().attr("content")),
//                            ZoneId.of("UTC"));

//                    <meta itemprop="datePublished" content="2021-06-14T9:28:44+0330">

                    String pubDate = articleDescription.select("meta[itemprop=datePublished]").first().attr("content");

                    String date;
                    String time;

                    if (pubDate.indexOf("T") == 9) {
                        date = pubDate.substring(0, 8) + "0" + pubDate.substring(8, 9);
                    } else {
                        date = pubDate.substring(0, pubDate.indexOf("T"));
                    }
                    if (pubDate.indexOf("+") - pubDate.indexOf("T") - 1 == 7) {
                        time = "0" + pubDate.substring(pubDate.indexOf("T") + 1);
                    } else {
                        time = pubDate.substring(pubDate.indexOf("T") + 1);
                    }

                    articleDatePublication = ZonedDateTime.of(LocalDateTime.parse(
                            (date + " " + time).substring(0, 19),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            ZoneId.of("Europe/Tallinn")
                    ).withZoneSameInstant(ZoneId.of("UTC"));

                    dateStamp = ZonedDateTime.now(ZoneId.of("UTC"));

                    int k = 0;
                    List<ArticleRubric> articleRubricList = new ArrayList<>();
                    Elements categories = articleDescription.select("span[class=link-category]").select("a");
                    if (!categories.isEmpty()) {
                        for (int j = 0; j < categories.size(); j++) {
                            rubricAliasName = categories.get(j).text();

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
                    articleCount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return articleCount;
    }
}
