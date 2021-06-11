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
@Table(name = "rubrics")
public class Rubric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "rubricId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<ArticleRubric> articleRubric = new ArrayList<>();

    @Column(length = 45)
    private String rubricKey;

    @Column(length = 45)
    private String aliasName;

    @Column(length = 45)
    private String name;

    @Transient
    private String range;

    private boolean active;
    private ZonedDateTime dateStamp;

    public Rubric() {
    }

    public Rubric(String aliasName, boolean active, ZonedDateTime dateStamp) {
        this.aliasName = aliasName;
        this.active = active;
        this.dateStamp = dateStamp;
    }

    @Override
    public String toString() {
        return "Rubric{" +
                "id=" + id +
                ", articleRubric=" + articleRubric +
                ", rubricKey='" + rubricKey + '\'' +
                ", aliasName='" + aliasName + '\'' +
                ", name='" + name + '\'' +
                ", range='" + range + '\'' +
                ", active=" + active +
                ", dateStamp=" + dateStamp +
                '}';
    }
}
