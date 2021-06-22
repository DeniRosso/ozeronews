package com.example.ozeronews.repo;

import com.example.ozeronews.models.NewsResource;

public interface NewsResourceRepository {

    Iterable<NewsResource> findAll();

    Iterable<NewsResource> findBySubscriptions(String subscriptions);

    Iterable<NewsResource> findTopResources(int count);

    Iterable<NewsResource> findAllResources();

    NewsResource findById(Long id);

    NewsResource findByResourceKey(String resourceKey);

    Iterable<NewsResource> findByCategories(String resourceListIds);

    boolean checkByResourceKey(String resourceKey);

    int[] updateResource(NewsResource newsResource);

    int[] saveResource(NewsResource newsResource);

}
