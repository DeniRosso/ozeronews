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
@Table(name = "news_resources")
public class NewsResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "resourceId", fetch = FetchType.LAZY)
    private List<Article> article = new ArrayList<>();

    @OneToMany(mappedBy = "resourceId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Subscription> subscription = new ArrayList<>();

    @Column(unique = true, length = 45)
    private String resourceKey;

    @Column(length = 45)
    private String shortName;

    @Column(length = 45)
    private String fullName;

    @Column(length = 120)
    private String resourceLink;

    @Column(length = 120)
    private String newsLink;
    private boolean active;
    private ZonedDateTime dateStamp;

    public NewsResource() {
    }

    @Override
    public String toString() {
        return "NewsResource{" +
                "id=" + id +
                ", article=" + article +
                ", subscription=" + subscription +
                ", resourceKey='" + resourceKey + '\'' +
                ", shortName='" + shortName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", resourceLink='" + resourceLink + '\'' +
                ", newsLink='" + newsLink + '\'' +
                ", active=" + active +
                ", dateStamp=" + dateStamp +
                '}';
    }
}
