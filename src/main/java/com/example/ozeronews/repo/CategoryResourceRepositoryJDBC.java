package com.example.ozeronews.repo;

import com.example.ozeronews.models.CategoryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Repository
public class CategoryResourceRepositoryJDBC implements CategoryResourceRepository{

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public CategoryResourceRepositoryJDBC(JdbcTemplate jdbcTemplate,
                                          NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public int[] save(CategoryResource categoryResource) {
        String query = "INSERT INTO categories_resources (category_resource_key, name, number, active, date_stamp)" +
            " SELECT :categoryResourceKey, :name, :number, :active, :dateStamp FROM DUAL WHERE NOT EXISTS" +
            " (SELECT category_resource_key FROM categories_resources " +
                " WHERE category_resource_key LIKE :categoryResourceKey) LIMIT 1";
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(categoryResource);
        return namedParameterJdbcTemplate.batchUpdate(query, batch);
    }

    @Override
    public int[] update(CategoryResource categoryResource) {
        String query = "UPDATE categories_resources SET " +
            " category_resource_key = :categoryResourceKey, name = :name, number = :number, active = :active, date_stamp = :dateStamp " +
            " WHERE id = :id ";
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(categoryResource);
        return namedParameterJdbcTemplate.batchUpdate(query, batch);
    }

    @Override
    public Iterable<CategoryResource> findAll() {
        String query = "SELECT * FROM categories_resources ORDER BY number";
        return namedParameterJdbcTemplate.query(query, this::mapRowToCategoryResource);
    }

    @Override
    public Iterable<CategoryResource> findByActive(boolean active) {
        String query = "SELECT * FROM categories_resources WHERE active = :active ORDER BY number";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("active", active);
        return namedParameterJdbcTemplate.query(query, parameterSource, this::mapRowToCategoryResource);
    }

    @Override
    public CategoryResource findById(Long id) {
        String query = "SELECT * FROM categories_resources WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("id", id);
        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, this::mapRowToCategoryResource);
    }

    @Override
    public Iterable<CategoryResource> findByName(String name) {
        String query = "SELECT * FROM categories_resources WHERE name = :name";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("name", name);
        return namedParameterJdbcTemplate.query(query, parameterSource, this::mapRowToCategoryResource);
    }

    private CategoryResource mapRowToCategoryResource(ResultSet rs, int rowNum) throws SQLException {
        ZonedDateTime dateStamp = ZonedDateTime.of(
                LocalDateTime.parse(rs.getString("date_stamp").substring(0, 19),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Europe/Moscow"));

        CategoryResource categoryResource = new CategoryResource();
        categoryResource.setId(rs.getLong("id"));
        categoryResource.setCategoryResourceKey(rs.getString("category_resource_key"));
        categoryResource.setName(rs.getString("name"));
        categoryResource.setNumber(rs.getString("number"));
        categoryResource.setActive(rs.getBoolean("active"));
        categoryResource.setDateStamp(dateStamp);
        return categoryResource;
    }
}
