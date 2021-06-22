package com.example.ozeronews.repo;

import com.example.ozeronews.models.CategoryResource;
import com.example.ozeronews.models.NewsResource;
import com.example.ozeronews.models.ResourceToCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Repository
public class ResourceToCategoryRepositoryJDBC implements ResourceToCategoryRepository{

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private NewsResourceRepository newsResourceRepository;

    @Autowired
    public ResourceToCategoryRepositoryJDBC(JdbcTemplate jdbcTemplate,
                                            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                            NewsResourceRepository newsResourceRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.newsResourceRepository = newsResourceRepository;
    }

    @Override
    public int[] save(ResourceToCategory resourceToCategory) {
        String query = "INSERT INTO resources_to_categories (resource_id, category_resource_id, active, date_stamp) " +
                " SELECT ?, ?, ?, ? FROM DUAL WHERE NOT EXISTS " +
                " (SELECT resource_id, category_resource_id FROM resources_to_categories " +
                " WHERE resource_id = ? AND category_resource_id = ?) LIMIT 1";
        jdbcTemplate.update(query,
                resourceToCategory.getResourceId().getId(),
                resourceToCategory.getCategoryResourceId().getId(),
                resourceToCategory.isActive(),
                resourceToCategory.getDateStamp(),
                resourceToCategory.getResourceId().getId(),
                resourceToCategory.getCategoryResourceId().getId()
        );
        return new int[1];
    }

    @Override
    public int[] update(ResourceToCategory resourceToCategory) {
        String query = "UPDATE resources_to_categories SET " +
                " resource_id = ?, category_resource_id = ?, active = ?, date_stamp = ? " +
                " WHERE id = ? ";
        jdbcTemplate.update(query,
                resourceToCategory.getResourceId().getId(),
                resourceToCategory.getCategoryResourceId().getId(),
                resourceToCategory.isActive(),
                resourceToCategory.getDateStamp(),
                resourceToCategory.getId()
        );
        return new int[1];
    }

    @Override
    public Iterable<ResourceToCategory> findByResource(NewsResource newsResource, CategoryResource categoryResource) {
        String query = "SELECT * FROM resources_to_categories " +
                " WHERE resource_id = :resourceId AND category_resource_id = :categoryResourceId ";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("resourceId", newsResource.getId())
                .addValue("categoryResourceId", categoryResource.getId());
        return namedParameterJdbcTemplate.query(query, parameterSource, this::mapRowToResourceToCategory);
    }

    @Override
    public Iterable<ResourceToCategory> findByCategory(CategoryResource categoryResource) {
        String query = "SELECT * FROM resources_to_categories " +
                " WHERE category_resource_id = :categoryResourceId ";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("categoryResourceId", categoryResource.getId());
        return namedParameterJdbcTemplate.query(query, parameterSource, this::mapRowToResourceToCategory);
    }

    private ResourceToCategory mapRowToResourceToCategory(ResultSet rs, int rowNum) throws SQLException {
        ZonedDateTime dateStamp = ZonedDateTime.of(
                LocalDateTime.parse(rs.getString("date_stamp").substring(0, 19),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Europe/Moscow"));

        ResourceToCategory resourceToCategory = new ResourceToCategory();
        resourceToCategory.setId(rs.getLong("id"));
        resourceToCategory.setResourceId(newsResourceRepository.findById(rs.getLong("resource_id")));
        resourceToCategory.setCategoryResourceId(new CategoryResource());
        resourceToCategory.setActive(rs.getBoolean("active"));
        resourceToCategory.setDateStamp(dateStamp);
        return resourceToCategory;
    }
}
