package com.example.ozeronews.service.parsing;

import com.example.ozeronews.models.Article;
import com.example.ozeronews.models.ArticleRubric;
import com.example.ozeronews.models.NewsResource;
import com.example.ozeronews.repo.ArticleRepository;
import com.example.ozeronews.service.ArticleSaveService;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
public class ParsingMeduza {

    @Autowired
    private ArticleSaveService articleSaveService;

    @Autowired
    private ArticleRepository articleRepository;

    @Value("${article.collection.count}")
    private int articleCollectionCount;

    public void getArticles() {
        String newsResourceKey = "meduza";
        String newsResourceLink = "https://meduza.io";
        String newsLink = "https://meduza.io/rss2/all";
        String articleTitle;
        String articleLink;
        String articleNumber;
        String articleImage = null;
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

                https://meduza.io/news/2021/05/26/air-france-otmenila-reys-parizh-moskva

                articleNumber = articleLink.substring(23).replace("/", "");
//                articleNumber = articleNumber.replace("/", "");
                articleNumber = newsResourceKey + "_" +
                        (articleNumber.length() >= 38 ? articleNumber.substring(0, 38) : articleNumber);

                if (articleRepository.checkByArticleNumber(articleNumber)) break;

                List<SyndEnclosure> enclosures = feed.getEntries().get(i).getEnclosures();
                if(enclosures!=null) {
                    for(SyndEnclosure enclosure : enclosures) {
                        if(enclosure.getType()!=null && enclosure.getType().equals("image/png")){
                            articleImage = enclosure.getUrl();
                        }
                    }
                }

                articleDatePublication = ZonedDateTime.ofInstant(
                        Instant.parse(feed.getEntries().get(i).getPublishedDate().toInstant().toString()),
                        ZoneId.of("UTC"));

                dateStamp = ZonedDateTime.now(ZoneId.of("UTC"));

                // Получение дополнительной информаций по статье
                Document docArticleDescription = Jsoup.connect(articleLink)
                        .userAgent("Safari")
                        .get();

                Elements articleDescription = docArticleDescription.getElementsByClass("GeneralMaterial-container");

                int k = 0;
                List<ArticleRubric> articleRubricList = new ArrayList<>();
                for (int j = 0; j < articleDescription.size(); j++) {
                    rubricAliasName = articleDescription.get(j).select("div[class=Tag-module_root__3IqWC Tag-module_large__22z_o Tag-module_gold__2ksA7 Tag-module_rich__2URd9]").text();
                    if (!rubricAliasName.isEmpty())
                        rubricAliasName = rubricAliasName.substring(0, 1).toUpperCase() + rubricAliasName.substring(1);
                    if (rubricAliasName.length() >= 45) rubricAliasName = rubricAliasName.substring(0 ,44);
                    articleRubricList.add(k++, new ArticleRubric().addRubricName(rubricAliasName, true, dateStamp));
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
//                System.out.println("article (Meduza) = " + article);
            }
        } catch (IOException | FeedException e) {
            e.printStackTrace();
        }
    }
}
