package com.example.ozeronews.service;

import com.example.ozeronews.models.Article;
import com.example.ozeronews.repo.ArticleRepository;
import com.example.ozeronews.repo.ArticleRubricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class ArticleDeleteService {

    private ArticleRepository articleRepository;
    private ArticleRubricRepository articleRubricRepository;

    @Autowired
    public ArticleDeleteService(ArticleRepository articleRepository,
                                ArticleRubricRepository articleRubricRepository) {
        this.articleRepository = articleRepository;
        this.articleRubricRepository = articleRubricRepository;
    }

    public void deleteArticles(ZonedDateTime date) {

        // Поиск articleRubrics по date
        List<Article> articles = (List<Article>) articleRepository.findByDate(date);
        if (articles.isEmpty()) return;

        // Составление ID из articleRubrics в articleRubricsListIds
        StringBuilder articleRubricList = new StringBuilder();
        for (Article article : articles) {
            articleRubricList.append(article.getId()).append(", ");
        }
        if (articleRubricList.length() > 0) {
            articleRubricList.replace(0, articleRubricList.length(),
                    String.valueOf(articleRubricList.substring(0, articleRubricList.length() - 2)));
        }
        String listIds = String.valueOf(articleRubricList);

        // Удаление записей из таблицы articles_rubrics по списку listIds
        articleRubricRepository.delete(listIds);

        // Удаление записей из таблицы articles по списку listIds
        articleRepository.delete(listIds);
    }
}
