package com.example.ozeronews.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "resources_to_categories")
public class ResourceToCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "resourceId")
    private NewsResource resourceId;

    @ManyToOne()
    @JoinColumn(name = "categoryResourceId")
//    @OnDelete(action = OnDeleteAction.CASCADE)
    private CategoryResource categoryResourceId;

    private boolean active;
    private ZonedDateTime dateStamp;

    @Override
    public String toString() {
        return "ResourceToCategory{" +
                "id=" + id +
                ", resourceId=" + resourceId +
                ", categoryResourceId=" + categoryResourceId +
                ", active=" + active +
                ", dateStamp=" + dateStamp +
                '}';
    }
}
