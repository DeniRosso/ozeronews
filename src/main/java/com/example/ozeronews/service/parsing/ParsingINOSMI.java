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
public class ParsingINOSMI {

    @Autowired
    private ArticleSaveService articleSaveService;

    @Autowired
    private ArticleRepository articleRepository;

    @Value("${article.collection.count}")
    private int articleCollectionCount;

    public int getArticles() {
        Document doc;
        int articleCount = 0;
        String resourceKey = "inosmi";
        String resourceFullName = "ИноСМИ";
        String resourceShortName = "ИноСМИ";
        String resourceLink = "https://inosmi.ru";
        String resourceNewsLink = "https://inosmi.ru/today";

        String articleTitle;
        String articleLink;
        String articleNumber;
        String articleImage;
        String rubricAliasName;
        ZonedDateTime articleDatePublication;
        ZonedDateTime dateStamp;

        try {
            // Полечение web страницы
            doc = Jsoup.connect(resourceNewsLink)
                    .userAgent("Safari") //userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();

            //<article class="rubric-list__article"><a href="/military/20210326/249425696.html" class="rubric-list__article-image rubric-list__article-image_small"><img src="https://cdn2.img.inosmi.ru/images/24033/64/240336405.jpg" width="194" height="146" alt="Военнослужащий у зенитного ракетного комплекса (ЗРК) &quot;Триумф&quot; С-400" title="Военнослужащий у зенитного ракетного комплекса (ЗРК) &quot;Триумф&quot; С-400"></a><div class="rubric-list__article-content"><h1 class="rubric-list__article-title rubric-list__article-title_small"><a href="/military/20210326/249425696.html">TNI: Америке не дает покоя российский ЗРК С-400</a></h1><p class="rubric-list__article-announce"><a href="/military/20210326/249425696.html">Иран и Китай имеют на вооружении российские ЗРК С-400, и это вызывает озабоченность у США. Но как быть с Индией и Турцией? Они также входят в группу заказчиков С-400, при этом являясь союзниками США. Будут ли против них введены санкции?</a></p></div><div class="rubric-list__article-right"><div class="rubric-list__article-right-table"><div class="rubric-list__article-right-row"><div class="rubric-list__article-right-cell rubric-list__article-right-cell_top"><address class="article-magazine rubric-list__article-magazine"><a href="/nationalinterest_org/" class="article-magazine__image"><img src="https://cdn1.img.inosmi.ru/images/23855/74/238557460.gif" width="74" height="36" alt="Логотип The National Interest" title="Логотип The National Interest"></a><a href="/nationalinterest_org/" rel="publisher">The National Interest</a>, <a href="/magazines/?country=country_usa">США</a></address><time class="rubric-list__article-date" datetime="2021-03-26T06:37:43+03:00">26.03.2021</time></div></div><div class="rubric-list__article-right-row"><div class="rubric-list__article-right-cell rubric-list__article-right-cell_middle"><div class="article-stats"><a class="article-stats__item article-stats__item_comments" href="/military/20210326/249425696.html#comments"><i class="inosmi-icon inosmi-icon_comment"></i>6</a><span class="article-stats__item article-stats__item_views"><i class="inosmi-icon inosmi-icon_eye"></i>3761</span></div></div></div><div class="rubric-list__article-right-row"><div class="rubric-list__article-right-cell rubric-list__article-right-cell_bottom"><aside class="article-social article-social_rubric"><div class="social-likes social-likes_visible social-likes_ready" data-url="https://inosmi.ru/military/20210326/249425696.html" data-title="The National Interest (США): почему Америке не дает покоя российский ЗРК С-400" data-counters="yes"><div class="social-likes__widget social-likes__widget_facebook" title="Поделиться ссылкой на Facebook"><span class="social-likes__button social-likes__button_facebook"><span class="social-likes__icon social-likes__icon_facebook"></span></span><span class="social-likes__counter social-likes__counter_facebook social-likes__counter_empty"></span></div><div class="social-likes__widget social-likes__widget_vkontakte" title="Поделиться ссылкой во Вконтакте"><span class="social-likes__button social-likes__button_vkontakte"><span class="social-likes__icon social-likes__icon_vkontakte"></span></span><span class="social-likes__counter social-likes__counter_vkontakte social-likes__counter_empty"></span></div><div class="social-likes__widget social-likes__widget_odnoklassniki" title="Поделиться ссылкой в Одноклассниках"><span class="social-likes__button social-likes__button_odnoklassniki"><span class="social-likes__icon social-likes__icon_odnoklassniki"></span></span></div><div class="social-likes__widget social-likes__widget_twitter" title="Поделиться ссылкой в Twitter"><span class="social-likes__button social-likes__button_twitter"><span class="social-likes__icon social-likes__icon_twitter"></span></span><span class="social-likes__counter social-likes__counter_twitter social-likes__counter_empty"></span></div></div><script>$('.social-likes').socialLikes({ counters: true });</script></aside></div></div></div></div></article>

            // Получение нужных статей
            Elements articles = doc.getElementsByTag("article");

            // Получение элементов по каждой статье
            for (int i = 0; i < articleCollectionCount; i++) {
                articleTitle = articles.get(i).select("h1[class=rubric-list__article-title rubric-list__article-title_small]").text();
                articleLink = resourceLink + articles.get(i).select("h1[class=rubric-list__article-title rubric-list__article-title_small]").
                        select("a").attr("href");
                articleNumber = resourceKey + "_" + articleLink.substring(articleLink.length()-14).substring(0, 9);
                articleImage = articles.get(i).select("img").attr("src");
                articleDatePublication = ZonedDateTime.parse((articles.get(i).
                        select("time[class=rubric-list__article-date]").attr("datetime")))
                        .withZoneSameInstant(ZoneId.of("UTC"));

//                articleDatePublication = ZonedDateTime.ofInstant(
//                        feed.getEntries().get(i).getPublishedDate().toInstant(),
//                        ZoneId.of("UTC"));

                if (articleRepository.checkByArticleNumber(articleNumber)) break;
                dateStamp = ZonedDateTime.now(ZoneId.of("UTC"));

                // Получение дополнительной информаций по статье
                Document docArticleDescription = Jsoup.connect(articleLink)
                        .userAgent("Safari")
                        .get();

                Elements articleDescription = docArticleDescription.getElementsByTag("article");

                int k = 0;
                List<ArticleRubric> articleRubricList = new ArrayList<>();
                rubricAliasName = articleDescription.
                        select("h1[class=heading heading_large heading_blue heading_no-border]").
                        select("a").text();

                if (rubricAliasName.length() >= 45) rubricAliasName = rubricAliasName.substring(0 ,44);

                articleRubricList.add(k++, new ArticleRubric().addRubricName(rubricAliasName, true, dateStamp));

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return articleCount;
    }
}
