package com.example.ozeronews.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "articles_rubrics")
public class ArticleRubric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "articleId")
//    @OnDelete(action = OnDeleteAction.CASCADE)
    private Article articleId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rubricId")
    private Rubric rubricId;

    public ArticleRubric() {
    }

    public ArticleRubric(Long id, Article articleId, Rubric rubricId) {
        this.id = id;
        this.articleId = articleId;
        this.rubricId = rubricId;
    }

    public ArticleRubric(Article articleId, Rubric rubricId) {
        this.articleId = articleId;
        this.rubricId = rubricId;
    }

    public ArticleRubric addRubricName(String rubricName, boolean active, ZonedDateTime dateStamp) {
        Rubric rubric = new Rubric(rubricName, active, dateStamp);
        ArticleRubric articleRubric = new ArticleRubric();
        articleRubric.setRubricId(rubric);
        return articleRubric;
    }

    @Override
    public String toString() {
        return "ArticleRubric{" +
                "id=" + id +
                ", articleId=" + articleId +
                ", rubricId=" + rubricId +
                '}';
    }
}
