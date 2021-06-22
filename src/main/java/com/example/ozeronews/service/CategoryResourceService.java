package com.example.ozeronews.service;

import com.example.ozeronews.models.*;
import com.example.ozeronews.repo.CategoryResourceRepository;
import com.example.ozeronews.repo.NewsResourceRepository;
import com.example.ozeronews.repo.ResourceToCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryResourceService {

    private NewsResourceRepository newsResourceRepository;
    private CategoryResourceRepository categoryResourceRepository;
    private ResourceToCategoryRepository resourceToCategoryRepository;

    @Autowired
    public CategoryResourceService(NewsResourceRepository newsResourceRepository,
                                   CategoryResourceRepository categoryResourceRepository,
                                   ResourceToCategoryRepository resourceToCategoryRepository) {
        this.newsResourceRepository = newsResourceRepository;
        this.categoryResourceRepository = categoryResourceRepository;
        this.resourceToCategoryRepository = resourceToCategoryRepository;
    }

    public Iterable<NewsResource> viewAllCategoryResource() {

        // Получить все NewsResource с метками категорий
        Iterable<NewsResource> newsResources = newsResourceRepository.findAll();
        return newsResources;
    }

    public Iterable<NewsResource> filterCategoryResource(CategoryResource categoryResource) {

        // Получить все NewsResource с метками категорий
        Iterable<NewsResource> resources = newsResourceRepository.findAll();
        Iterable<ResourceToCategory> resourceToCategories = resourceToCategoryRepository.findByCategory(categoryResource);

        // Получить NewsResource для текущей CategoryResource
        for (NewsResource resource : resources) {
            for (ResourceToCategory resourceToCategory : resourceToCategories) {
                if (resource.getId().equals(resourceToCategory.getResourceId().getId()) &&  resourceToCategory.isActive()) {
                    resource.setResourceToCategories((List<ResourceToCategory>) resourceToCategories);
                }
            }
        }
        return resources;
    }

    public Iterable<NewsResource> filterCategoryResourceById(CategoryResource categoryResource) {
        // Получить Iterable<ResourceToCategory> по categoryResource.getId()
        Iterable<ResourceToCategory> resourceToCategories = resourceToCategoryRepository.findByCategory(categoryResource);

        // Составить строку перечисления rubrics_id
        StringBuilder resourceList = new StringBuilder();
        for (ResourceToCategory resourceToCategory : resourceToCategories) {
            if (resourceToCategory.isActive()) {
                assert false;
                //rubricsList.append("'").append(rubric.getId()).append("'").append(", ");
                resourceList.append(resourceToCategory.getResourceId().getId()).append(", ");
            }
        }
        if (resourceList.length() > 0) {
            resourceList.replace(0, resourceList.length(),
                    String.valueOf(resourceList.substring(0, resourceList.length() - 2)));
        }
        String resourceListIds = String.valueOf(resourceList);

        // Получить Iterable<NewsResource> по ResourceToCategory.getResourceId()
        Iterable<NewsResource> resources = newsResourceRepository.findByCategories(resourceListIds);
        return resources;
    }

    public void createResourceToCategory(NewsResource resource, CategoryResource categoryResource) {

        ResourceToCategory resourceToCategory = new ResourceToCategory();
        List<ResourceToCategory> resourceToCategoryIterable =
                (List<ResourceToCategory>) resourceToCategoryRepository.findByResource(resource, categoryResource);

        if (resourceToCategoryIterable.size() > 0) {
            resourceToCategory = resourceToCategoryIterable.iterator().next();
            if (resourceToCategory.isActive()) {
                resourceToCategory.setActive(false);
            } else {
                resourceToCategory.setActive(true);
            }
            resourceToCategory.setResourceId(resource);
            resourceToCategory.setCategoryResourceId(categoryResource);
            resourceToCategory.setDateStamp(ZonedDateTime.now(ZoneId.of("UTC")));
            resourceToCategoryRepository.update(resourceToCategory);
        } else {
            resourceToCategory.setResourceId(resource);
            resourceToCategory.setCategoryResourceId(categoryResource);
            resourceToCategory.setActive(true);
            resourceToCategory.setDateStamp(ZonedDateTime.now(ZoneId.of("UTC")));
            resourceToCategoryRepository.save(resourceToCategory);
        }
    }
}
