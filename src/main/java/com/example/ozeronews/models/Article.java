package com.example.ozeronews.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resourceId")
    private NewsResource resourceId;

    @OneToMany(mappedBy = "articleId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ArticleRubric> articleRubric = new ArrayList<>();

    @Column(unique = true, length = 45)
    private String number;

    @Column(length = 255)
    private String title;

    @Column(length = 255)
    private String link;

    @Column(length = 255)
    private String image;

    @Transient
    private String periodPublication;

    private ZonedDateTime datePublication;
    private ZonedDateTime dateStamp;

    public Article() {
    }

    public Article(Long id, NewsResource resourceId, List<ArticleRubric> articleRubric, String number, String title, String link, String image, String periodPublication, ZonedDateTime datePublication, ZonedDateTime dateStamp) {
        this.id = id;
        this.resourceId = resourceId;
        this.articleRubric = articleRubric;
        this.number = number;
        this.title = title;
        this.link = link;
        this.image = image;
        this.periodPublication = periodPublication;
        this.datePublication = datePublication;
        this.dateStamp = dateStamp;
    }

    public Article(NewsResource resourceId, List<ArticleRubric> articleRubric, String number, String title, String link, String image, String periodPublication, ZonedDateTime datePublication, ZonedDateTime dateStamp) {
        this.resourceId = resourceId;

        this.articleRubric = articleRubric;
        this.number = number;
        this.title = title;
        this.link = link;
        this.image = image;
        this.periodPublication = periodPublication;
        this.datePublication = datePublication;
        this.dateStamp = dateStamp;
    }

    public Article(NewsResource resourceId, String number, String title, String link, String image, ZonedDateTime datePublication, ZonedDateTime dateStamp) {
        this.resourceId = resourceId;
        this.number = number;
        this.title = title;
        this.link = link;
        this.image = image;
        this.datePublication = datePublication;
        this.dateStamp = dateStamp;
    }

    public Article(List<ArticleRubric> articleRubric) {
        this.articleRubric = articleRubric;
    }


    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", resourceId='" + resourceId + '\'' +
                ", articleRubric=" + articleRubric +
                ", number='" + number + '\'' +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", image='" + image + '\'' +
                ", periodPublication='" + periodPublication + '\'' +
                ", datePublication=" + datePublication +
                ", dateStamp='" + dateStamp + '\'' +
                '}';
    }
}
