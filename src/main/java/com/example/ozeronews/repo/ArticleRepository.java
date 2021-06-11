package com.example.ozeronews.repo;

import com.example.ozeronews.models.Article;

import java.time.ZonedDateTime;

public interface ArticleRepository {

    Iterable<Article> findArticleById(Long Id, int startNumber, int linesNumber);

    Iterable<Article> findAllArticlesByLimit(int startNumber, int linesNumber);

    Iterable<Article> findArticlesByResource(Long resourceId, int startNumber, int linesNumber);

    Iterable<Article> findArticlesByRubric(String resourceId, int startNumber, int linesNumber);

    Iterable<Article> findByDate(ZonedDateTime date);

    Iterable<Article> findArticlesBySearch(String search, int startNumber, int linesNumber);

    Iterable<Article> findArticlesBySubscriptions(String subscriptionsListId, int startNumber, int linesNumber);

    Iterable<Article> findSubscriptionsArticlesBySearch(String search, String subscriptionsListId, int startNumber, int linesNumber);

    Article findByMaxId();

    boolean checkByArticleNumber(String articleNumber);

    int[] saveArticle(Article article);

    int delete(ZonedDateTime dateStamp);

    int delete(String listIds);

}
