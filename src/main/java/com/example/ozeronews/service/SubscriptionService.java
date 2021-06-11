package com.example.ozeronews.service;

import com.example.ozeronews.models.NewsResource;
import com.example.ozeronews.models.Subscription;
import com.example.ozeronews.models.User;
import com.example.ozeronews.repo.NewsResourceRepository;
import com.example.ozeronews.repo.SubscriptionRepository;
import com.example.ozeronews.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubscriptionService {

    private SubscriptionRepository subscriptionRepository;
    private NewsResourceRepository newsResourceRepository;
    private UserRepository userRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               NewsResourceRepository newsResourceRepository,
                               UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.newsResourceRepository = newsResourceRepository;
        this.userRepository = userRepository;
    }

    public Iterable<Subscription> findAllByUser(User user) {
        List<Subscription> subscriptionsList = new ArrayList<>();
        // Получить Iterable<NewsResources> из news_resources с active = true
        Iterable<NewsResource> newsResources = newsResourceRepository.findAll();

        // Получить все подписки user
        Iterable<Subscription> subscriptions = subscriptionRepository.findByUser(user);

        // Получение user
        user = userRepository.findByEmail(user.getEmail());

        // Цикл по newsResources
        for (NewsResource newsResource : newsResources) {
            Subscription subscriptionUser = new Subscription();
            subscriptionUser.setUserId(user);
            subscriptionUser.setResourceId(newsResource);
            subscriptionUser.setActive(false);
            subscriptionUser.setDateStamp(null);
            subscriptionsList.add(subscriptionUser);
        }
        for (Subscription subscriptionUser : subscriptionsList) {
            for (Subscription subscription : subscriptions) {
                if (subscriptionUser.getResourceId().getId().equals(subscription.getResourceId().getId())) {
                    subscriptionUser.setActive(subscription.isActive());
                    subscriptionUser.setDateStamp(subscription.getDateStamp());
                }
            }
        }
        return subscriptionsList;
    }

    public Iterable<Subscription> findByUser(User user) {
        List<Subscription> subscriptionsList = new ArrayList<>();
        // Получить все подписки user
        List<Subscription> subscriptions = (List<Subscription>) subscriptionRepository.findByUser(user);
        if (subscriptions.isEmpty()) return null;

        // Получить все NewsResources из news_resources с active = true
        Iterable<NewsResource> newsResources = newsResourceRepository.findAll();

        // Цикл по newsResources
        for (Subscription subscription : subscriptions) {
            if (subscription.isActive()) {
                for (NewsResource newsResource : newsResources) {
                    if (subscription.getResourceId().getId().equals(newsResource.getId())) {
                        subscription.setResourceId(newsResource);
                    }
                }
                subscriptionsList.add(subscription);
            }
        }
        return subscriptionsList;
    }

    public void createSubscription(Subscription subscription) {
        if (subscription.isActive()) {
            subscription.setActive(false);
        } else {
            subscription.setActive(true);
        }
        subscription.setDateStamp(ZonedDateTime.now(ZoneId.of("UTC")));

        // Проверить есть ли уже subscription по user_id и resource_id
        if (subscriptionRepository.checkBySubscription(subscription)) {
            // Если да обновить
            subscriptionRepository.updateSubscription(subscription);
        } else {
            // Если нет - создать
            subscriptionRepository.saveSubscription(subscription);
        }
    }
}
