package com.example.ozeronews.service.parsing;

import com.example.ozeronews.models.Article;
import com.example.ozeronews.models.ArticleRubric;
import com.example.ozeronews.models.NewsResource;
import com.example.ozeronews.repo.ArticleRepository;
import com.example.ozeronews.service.ArticleSaveService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ParsingProekt {

    @Autowired
    private ArticleSaveService articleSaveService;

    @Autowired
    private ArticleRepository articleRepository;

    @Value("${article.collection.count}")
    private int articleCollectionCount;

    public int getArticles() {
        int articleCount = 0;
        String newsResourceKey = "proekt";
        String newsResourceLink = "https://www.proekt.media/";
        String newsLink = "https://www.proekt.media/";
        String articleTitle;
        String articleLink;
        String articleNumber;
        String articleImage;
        String rubricAliasName;
        ZonedDateTime articleDatePublication;
        ZonedDateTime dateStamp;

//<div class="tile tile--investigation tile--large order-1 tile--9428888" style="background-color: #260e14;">
//  <style></style>
//    <div class="tile__backdrop absolute-image"></div>
//    <a href="https://www.proekt.media/investigation/domogatelstva-mgu-sergeev/" class="absolute-link"></a>
//    <img src="https://proektmedia-stat.ams3.digitaloceanspaces.com/2021/06/sergeev_ill_purple-1024x855.jpg" srcset="https://proektmedia-stat.ams3.digitaloceanspaces.com/2021/06/sergeev_ill_purple.jpg 1080w, https://proektmedia-stat.ams3.digitaloceanspaces.com/2021/06/sergeev_ill_purple-1024x855.jpg 1024w, https://proektmedia-stat.ams3.digitaloceanspaces.com/2021/06/sergeev_ill_purple-512x428.jpg 512w, https://proektmedia-stat.ams3.digitaloceanspaces.com/2021/06/sergeev_ill_purple-64x53.jpg 64w, https://proektmedia-stat.ams3.digitaloceanspaces.com/2021/06/sergeev_ill_purple-256x214.jpg 256w" width="512" height="428" sizes="(min-width: 320px) and (max-width: 639px) 1024px, (min-width: 640px) 1440px, 1024px" class="tile__image">
//    <div class="tile__text">
//      <span class="tile__title">Физика твердого тела. </span>
//      <span class="tile__subtitle">
//      <span class="tile__category" style="color: #f55b4f">Расследование</span> о&nbsp;многолетних домогательствах к&nbsp;ученикам элитной школы при&nbsp;МГУ  </span>
//    </div>
//  <footer class="tile__footer">
//    <div class="tile__author"><nobr>Юлия Лукьянова</nobr></div>
//      <div class="tile__date">
//          <svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" width="9px" height="9px" viewBox="0 0 9 9" style="enable-background:new 0 0 9 9;" xml:space="preserve">
//              <path fill="white" d="M4.5,0C2,0,0,2,0,4.5S2,9,4.5,9S9,7,9,4.5S7,0,4.5,0z M4.5,6C3.7,6,3,5.3,3,4.5C3,3.7,3.7,3,4.5,3S6,3.7,6,4.5
//                  C6,5.3,5.3,6,4.5,6z">
//              </path>
//          </svg>
//           17.06.2021
//      </div>
//  </footer>
//</div>


        try {
            // Полечение web страницы
            Document doc = Jsoup.connect(newsLink)
                .userAgent("Mozilla/5.0")
                .referrer("https://www.google.com")
                .get();

            // Получение нужных статей
            Elements articles = doc.select("div[class=tile]");

            // Получение элементов по каждой статье
            for (int i = 0; i < articleCollectionCount; i++) {

                articleTitle = null;
                articleLink = null;
                articleNumber = null;
                articleImage = null;
                rubricAliasName = null;
                articleDatePublication = null;
                dateStamp = null;

                articleTitle = articles.get(i).select("span[class=tile__title]").first().text();
                articleLink = articles.get(i).select("a[class=absolute-link]").first().attr("href");
                articleNumber = newsResourceKey + "_" + articleLink.substring("https://www.proekt.media/".length());
                articleNumber = (articleNumber.length() >= 45 ? articleNumber.substring(0, 45) : articleNumber);

                if (articleRepository.checkByArticleNumber(articleNumber)) break;

                System.out.println("date = " + articles.get(i).select("div[class=tile__date]").first().text());

//                articleDatePublication = ZonedDateTime.of(LocalDateTime.parse(
//                        (LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd ")) +
//                                articles.get(i).select("div[class=tile__date]").text() + "00:00:00"),
//                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
//                        ZoneId.of("Europe/Moscow")
//                ).withZoneSameInstant(ZoneId.of("UTC"));

                dateStamp = ZonedDateTime.now(ZoneId.of("UTC"));

                articleImage = articles.select("img[class=tile__image]").first().attr("src");

                int k = 0;
                List<ArticleRubric> articleRubricList = new ArrayList<>();
                for (int j = 0; j < articles.get(i).select("span[class=tile__category]").size(); j++) {
                    rubricAliasName = articles.get(i).select("span[class=tile__category]").get(j).text();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return articleCount;
    }
}
