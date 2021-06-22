package com.example.ozeronews.repo;

import com.example.ozeronews.models.CategoryResource;
import com.example.ozeronews.models.NewsResource;
import com.example.ozeronews.models.ResourceToCategory;

public interface ResourceToCategoryRepository {

    int[] save(ResourceToCategory resourceToCategory);

    int[] update(ResourceToCategory resourceToCategory);

    Iterable<ResourceToCategory> findByResource(NewsResource newsResource, CategoryResource categoryResource);

    Iterable<ResourceToCategory> findByCategory(CategoryResource categoryResource);
}
