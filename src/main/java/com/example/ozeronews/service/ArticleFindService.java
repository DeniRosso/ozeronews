package com.example.ozeronews.service;

import com.example.ozeronews.models.Article;
import com.example.ozeronews.models.NewsResource;
import com.example.ozeronews.models.Rubric;
import com.example.ozeronews.models.Subscription;
import com.example.ozeronews.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class ArticleFindService {

    private ArticleRepository articleRepository;
    private NewsResourceRepository newsResourcesRepository;
    private ArticleRubricRepository articleRubricRepository;
    private RubricRepository rubricRepository;
    private SubscriptionRepository subscriptionRepository;
    private UserRepository userRepository;
    private UserRepo userRepo;

    @Autowired
    public ArticleFindService(ArticleRepository articleRepository,
                              NewsResourceRepository newsResourcesRepository,
                              ArticleRubricRepository articleRubricRepository,
                              RubricRepository rubricRepository,
                              SubscriptionRepository subscriptionRepository,
                              UserRepository userRepository,
                              UserRepo userRepo) {
        this.articleRepository = articleRepository;

        this.newsResourcesRepository = newsResourcesRepository;
        this.articleRubricRepository = articleRubricRepository;
        this.rubricRepository = rubricRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.userRepo = userRepo;
    }

    public Iterable<Article> findAllArticlesByLimit(String timeZone, Long maxId, int startNumber, int linesNumber) {
        Iterable<Article> articles = articleRepository.findAllArticlesByLimit(maxId, startNumber, linesNumber);
        return setTimeToArticle(timeZone, articles);
    }

    public Iterable<Article> findArticlesByResource(String resourceKey, String timeZone, Long maxId, int startNumber, int linesNumber) {
        // ???????????????? id ???? news_resources ???? resource_key
        NewsResource newsResource = newsResourcesRepository.findByResourceKey(resourceKey);
        // ???????????????? ?????? ???????????? ???? resource_id
        Iterable<Article> articles = articleRepository.findArticlesByResource(newsResource.getId(), maxId, startNumber, linesNumber);
        return setTimeToArticle(timeZone, articles);
    }

    public Iterable<Article> findArticlesByRubric(String rubricKey, String timeZone, Long maxId, int startNumber, int linesNumber) {
        // ???????????????? id ???? rubrics ???? rubricKey
        Iterable<Rubric> rubrics = rubricRepository.findByRubricKey(rubricKey);
        // ???????????????? ?????? ???????????? ???? rubric_id
        // ?????????????????? ???????????? ???????????????????????? rubrics_id
        StringBuilder rubricsList = new StringBuilder();
        for (Rubric rubric : rubrics) {
            if (rubric.isActive()) {
                assert false;
                //rubricsList.append("'").append(rubric.getId()).append("'").append(", ");
                rubricsList.append(rubric.getId()).append(", ");
            }
        }
        if (rubricsList.length() > 0) {
            rubricsList.replace(0, rubricsList.length(),
                    String.valueOf(rubricsList.substring(0, rubricsList.length() - 2)));
        }
        String rubricsListIds = String.valueOf(rubricsList);
        Iterable<Article> articles = articleRepository.findArticlesByRubric(rubricsListIds, maxId, startNumber, linesNumber);
        return setTimeToArticle(timeZone, articles);
    }

    public Iterable<Article> findArticlesBySearch(String timeZone, String search, int startNumber, int linesNumber) {
        Iterable<Article> articles = articleRepository.findArticlesBySearch(search, startNumber, linesNumber);
        return setTimeToArticle(timeZone, articles);
    }

    public Iterable<Article> findArticlesBySubscriptions(String email, String timeZone, Long maxId, int startNumber, int linesNumber) {
        // ?????????? User
//        User user = userRepo.findByEmail(email);
        // ???? user.id ???????????????? ?????? subscriptions
        List<Subscription> subscriptions = (List<Subscription>) subscriptionRepository.findByUser(userRepo.findByEmail(email));
        if (subscriptions.isEmpty()) return null;

        // ?????????????????? ???????????? ???????????????????????? subscriptions.resource_id
        StringBuilder subscriptionsList = new StringBuilder();
        for (Subscription subscription : subscriptions) {
            if (subscription.isActive()) {
                assert false;
                //subscriptionsList.append("'").append(subscription.getResourceId().getId()).append("'").append(", ");
                subscriptionsList.append(subscription.getResourceId().getId()).append(", ");
            }
        }
        if (subscriptionsList.length() > 0) {
            subscriptionsList.replace(0, subscriptionsList.length(),
                    String.valueOf(subscriptionsList.substring(0, subscriptionsList.length() - 2)));
        }
        String subscriptionsListId = String.valueOf(subscriptionsList);
        // ???????????????? ?????? ???????????? ???? ???????????? ???? resource_id
        Iterable<Article> articles = articleRepository.findArticlesBySubscriptions(subscriptionsListId, maxId, startNumber, linesNumber);
        return setTimeToArticle(timeZone, articles);
    }

    public Iterable<Article> findSubscriptionsArticlesBySearch(
            String timeZone, String search, String email, int startNumber, int linesNumber) {
        // ?????????? User
//        User user = userRepo.findByEmail(email);
        // ???? user.id ???????????????? ?????? subscriptions
        List<Subscription> subscriptions = (List<Subscription>) subscriptionRepository.findByUser(userRepo.findByEmail(email));
        if (subscriptions.isEmpty()) return null;

        // ?????????????????? ???????????? ???????????????????????? subscriptions.resource_id
        StringBuilder subscriptionsList = new StringBuilder();
        for (Subscription subscription : subscriptions) {
            if (subscription.isActive()) {
                assert false;
                //subscriptionsList.append("'").append(subscription.getResourceId().getId()).append("'").append(", ");
                subscriptionsList.append(subscription.getResourceId().getId()).append(", ");
            }
        }
        if (subscriptionsList.length() > 0) {
            subscriptionsList.replace(0, subscriptionsList.length(),
                    String.valueOf(subscriptionsList.substring(0, subscriptionsList.length() - 2)));
        }
        String subscriptionsListId = String.valueOf(subscriptionsList);
        // ???????????????? ?????? ???????????? ???? ???????????? ???? resource_id
        Iterable<Article> articles = articleRepository.findSubscriptionsArticlesBySearch(search, subscriptionsListId, startNumber, linesNumber);
        return setTimeToArticle(timeZone, articles);
    }

    private Iterable<Article> setTimeToArticle(String timeZone, Iterable<Article> articles) {
        for (Article article : articles) {
            String period;
            long periodMinutes = Duration.between(
                    article.getDatePublication().withZoneSameInstant(ZoneId.of(timeZone)),
                    ZonedDateTime.now(ZoneId.of(timeZone))).toMinutes();
            if (periodMinutes >= 0 && periodMinutes < 60) {
                period = periodMinutes + "m";
            }  else if (periodMinutes >= 60 && periodMinutes <= 1440) {
                period= periodMinutes/60 + "h";
            } else if (periodMinutes >= 1440 && periodMinutes <= 14400) {
                period= periodMinutes/(60*24) + "d";
            } else {
                period = "";
            }

            article.setPeriodPublication(period);
            article.setDatePublication(article.getDatePublication().withZoneSameInstant(ZoneId.of(timeZone)));
        }
        return articles;

    }
}
