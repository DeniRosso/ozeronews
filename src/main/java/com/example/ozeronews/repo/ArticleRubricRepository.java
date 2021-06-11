package com.example.ozeronews.repo;

import com.example.ozeronews.models.Article;
import com.example.ozeronews.models.ArticleRubric;

import java.util.List;

public interface ArticleRubricRepository {

    int[] saveArticleRubric(Article article);

    List<ArticleRubric> findByArticleId(String articleId);

    List<ArticleRubric> findByRubricId(String rubricId);

    int delete(String listIds);

}
