package com.example.ozeronews.repo;

import com.example.ozeronews.models.Rubric;

public interface RubricRepository {

    Iterable<Rubric> findAll();

    Iterable<Rubric> findTopRubrics(int count);

    Iterable<Rubric> findAllRubrics();

    Rubric findById(Long id);

    Iterable<Rubric> findByRubricKey(String rubricKey);

    Rubric findByRubricAliasName(String rubricAliasName);

    int[] saveRubric(Rubric rubric);

    int[] updateRubric(Rubric rubric);
}
