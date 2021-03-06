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
public class ParsingJPGazeta {

    @Autowired
    private ArticleSaveService articleSaveService;

    @Autowired
    private ArticleRepository articleRepository;

    @Value("${article.collection.count}")
    private int articleCollectionCount;

    public int getArticles() {
        int articleCount = 0;
        String resourceKey = "jpgazeta";
        String resourceFullName = "Журналистская Правда";
        String resourceShortName = "Журналистская Правда";
        String resourceLink = "https://jpgazeta.ru/";
        String resourceNewsLink = "https://jpgazeta.ru/feed/";

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

                articleNumber = resourceKey + "_" + feed.getEntries().get(i).getUri()
                        .substring(feed.getEntries().get(i).getUri().lastIndexOf("p=") + 2);

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

//                <img width="1000" height="667"
//                  src="https://jpgazeta.ru/wp-content/uploads/2021/08/fbqg3zbhuq2zbuevn1epresoydplmdnf01ww5uwh.jpeg"
//                  data-src="https://jpgazeta.ru/wp-content/uploads/2021/08/fbqg3zbhuq2zbuevn1epresoydplmdnf01ww5uwh.jpeg"
//                  class="img-fluid wp-post-image lazy error" alt="" loading="lazy"
//                  data-srcset="https://jpgazeta.ru/wp-content/uploads/2021/08/fbqg3zbhuq2zbuevn1epresoydplmdnf01ww5uwh.jpeg 1000w, https://jpgazeta.ru/wp-content/uploads/2021/08/fbqg3zbhuq2zbuevn1epresoydplmdnf01ww5uwh-420x280.jpeg 420w, https://jpgazeta.ru/wp-content/uploads/2021/08/fbqg3zbhuq2zbuevn1epresoydplmdnf01ww5uwh-768x512.jpeg 768w, https://jpgazeta.ru/wp-content/uploads/2021/08/fbqg3zbhuq2zbuevn1epresoydplmdnf01ww5uwh-800x534.jpeg 800w" data-sizes="(max-width: 1000px) 100vw, 1000px" sizes="(max-width: 1000px) 100vw, 1000px" srcset="https://jpgazeta.ru/wp-content/uploads/2021/08/fbqg3zbhuq2zbuevn1epresoydplmdnf01ww5uwh.jpeg 1000w, https://jpgazeta.ru/wp-content/uploads/2021/08/fbqg3zbhuq2zbuevn1epresoydplmdnf01ww5uwh-420x280.jpeg 420w, https://jpgazeta.ru/wp-content/uploads/2021/08/fbqg3zbhuq2zbuevn1epresoydplmdnf01ww5uwh-768x512.jpeg 768w, https://jpgazeta.ru/wp-content/uploads/2021/08/fbqg3zbhuq2zbuevn1epresoydplmdnf01ww5uwh-800x534.jpeg 800w" data-was-processed="true">

                // Получение дополнительной информаций по статье
                Document docArticleDescription = Jsoup.connect(articleLink)
                        .userAgent("Safari")
                        .get();

                if (!docArticleDescription.getElementsByTag("main").first()
                        .getElementsByTag("article").first()
                        .select("img").select(".img-fluid").first()
                        .attr("data-src").isEmpty()) {
                    articleImage = docArticleDescription.getElementsByTag("main").first()
                            .getElementsByTag("article").first()
                            .select("img").select(".img-fluid").first().attr("data-src");
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
