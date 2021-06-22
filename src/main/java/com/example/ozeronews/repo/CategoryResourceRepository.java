package com.example.ozeronews.repo;

import com.example.ozeronews.models.CategoryResource;

public interface CategoryResourceRepository {

    int[] save(CategoryResource categoryResource);

    int[] update(CategoryResource categoryResource);

    Iterable<CategoryResource> findAll();

    Iterable<CategoryResource> findByActive(boolean active);

    CategoryResource findById(Long id);

    Iterable<CategoryResource> findByName(String name);
}
