package com.example.ozeronews.repo;

import com.example.ozeronews.models.NewsResource;
import com.example.ozeronews.models.Subscription;
import com.example.ozeronews.models.User;

public interface SubscriptionRepository {

    int[] saveSubscription(Subscription subscription);

    int[] updateSubscription(Subscription subscription);

    Iterable<Subscription> findBySubscription(Subscription subscription);

    boolean checkBySubscription(Subscription subscription);

    boolean checkByEmail(User user);

    Iterable<Subscription> findByUser(User user);

    Iterable<Subscription> findByUserNewsResources(User user, Iterable<NewsResource> newsResources);
}
