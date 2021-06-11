package com.example.ozeronews.service;

import com.example.ozeronews.models.Article;
import com.example.ozeronews.models.NewsResource;
import com.example.ozeronews.models.Rubric;
import com.example.ozeronews.repo.ArticleRepository;
import com.example.ozeronews.repo.ArticleRubricRepository;
import com.example.ozeronews.repo.NewsResourceRepository;
import com.example.ozeronews.repo.RubricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleSaveService {

    private ArticleRepository articleRepository;
    private NewsResourceRepository newsResourcesRepository;
    private ArticleRubricRepository articleRubricRepository;
    private RubricRepository rubricRepository;

    @Autowired
    public ArticleSaveService(ArticleRepository articleRepository,
                              NewsResourceRepository newsResourcesRepository,
                              ArticleRubricRepository articleRubricRepository,
                              RubricRepository rubricRepository) {
        this.articleRepository = articleRepository;
        this.newsResourcesRepository = newsResourcesRepository;
        this.articleRubricRepository = articleRubricRepository;
        this.rubricRepository = rubricRepository;
    }

    public void saveArticle(Article article) {

        // Запись в таблицу news_resources
        if (!newsResourcesRepository.checkByResourceKey(article.getResourceId().getResourceKey())) {
            newsResourcesRepository.saveResource(article.getResourceId());
        }

        // Получание ID ресурса из таблицы news_resources
        NewsResource newsResource = newsResourcesRepository.findByResourceKey(article.getResourceId().getResourceKey());

        // Добавления в article ID ресурса
        article.setResourceId(newsResource);

        // Запись в таблицу articles
        articleRepository.saveArticle(article);

        // Получение ID добавленной статьи из таблицы articles
        article.setId(articleRepository.findByMaxId().getId());

        // Цикл добавления рубрик
        for (int i = 0; i < article.getArticleRubric().size(); i++) {

            // Запись рубрики в таблицу rubrics если их еще нет
            rubricRepository.saveRubric(article.getArticleRubric().get(i).getRubricId());

            // Получени ID рубрики из таблицы rubrics по имени рубрики
            Rubric rubric = rubricRepository.findByRubricAliasName(article.getArticleRubric().get(i).getRubricId().getAliasName());
            article.getArticleRubric().get(i).getRubricId().setId(rubric.getId());
        }
        // Запись в таблицу articles_rubrics
        articleRubricRepository.saveArticleRubric(article);
    }
}
