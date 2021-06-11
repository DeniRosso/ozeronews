package com.example.ozeronews.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resourceId")
    private NewsResource resourceId;

    private boolean active;
    private ZonedDateTime dateStamp;


    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", userId=" + userId +
                ", resourceId=" + resourceId +
                ", active=" + active +
                ", dateStamp=" + dateStamp +
                '}';
    }
}
