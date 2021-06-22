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
@Table(name = "categories_resources")
public class CategoryResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "categoryResourceId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<ResourceToCategory> resourceToCategories = new ArrayList<>();

    @Column(unique = true, length = 45)
    private String categoryResourceKey;

    @Column(unique = true, length = 45)
    private String name;

    @Column(unique = true, length = 45)
    private String number;

    private boolean active;
    private ZonedDateTime dateStamp;

    @Override
    public String toString() {
        return "CategoryResource{" +
                "id=" + id +
                ", resourceToCategories=" + resourceToCategories +
                ", categoryResourceKey='" + categoryResourceKey + '\'' +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", active=" + active +
                ", dateStamp=" + dateStamp +
                '}';
    }
}
