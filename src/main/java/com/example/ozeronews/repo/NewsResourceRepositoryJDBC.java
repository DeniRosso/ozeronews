package com.example.ozeronews.repo;

import com.example.ozeronews.models.NewsResource;
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
public class NewsResourceRepositoryJDBC implements NewsResourceRepository {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public NewsResourceRepositoryJDBC(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public int[] saveResource(NewsResource newsResource) {
        String query = "INSERT INTO news_resources (resource_key, resource_link, news_link, active, date_stamp) " +
                " SELECT :resourceKey, :resourceLink, :newsLink, :active, :dateStamp FROM DUAL WHERE NOT EXISTS ( " +
                " SELECT resource_key FROM news_resources " +
                " WHERE resource_key LIKE :resourceKey " +
                " ) LIMIT 1 ";
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(newsResource);
        return namedParameterJdbcTemplate.batchUpdate(query, batch);
    }

    @Override
    public int[] updateResource(NewsResource newsResource) {
        String query = "UPDATE news_resources SET " +
                " full_name = :fullName, short_name = :shortName, " +
                " resource_link = :resourceLink, news_link = :newsLink, active = :active, date_stamp = :dateStamp " +
                " WHERE id = :id ";
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(newsResource);
        return namedParameterJdbcTemplate.batchUpdate(query, batch);
    }

    @Override
    public Iterable<NewsResource> findAll() {
        String query = "SELECT * FROM news_resources ORDER BY full_name";
        return namedParameterJdbcTemplate.query(query, this::mapRowToResource);
    }

    @Override
    public Iterable<NewsResource> findBySubscriptions(String subscriptions) {
        String query = "SELECT * FROM news_resources WHERE resource_key IN (:subscriptions)";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("subscriptions", subscriptions);
        return namedParameterJdbcTemplate.query(query, parameterSource, this::mapRowToResource);
    }

    @Override
    public Iterable<NewsResource> findTopResources(int count) {
        String query =
            "SELECT news_resources.id, news_resources.resource_key, news_resources.full_name, news_resources.short_name, " +
                " news_resources.resource_link, news_resources.news_link, news_resources.active, news_resources.date_stamp, " +
                " resource_id, COUNT(articles.resource_id) " +
            " FROM articles AS articles " +
            " LEFT JOIN (SELECT * FROM news_resources) AS news_resources " +
            " ON (articles.resource_id = news_resources.id) " +
            " WHERE news_resources.active = 1 " +
                "AND (news_resources.full_name IS NOT NULL OR news_resources.full_name NOT LIKE '') " +
            " GROUP BY articles.resource_id " +
            " ORDER BY COUNT(articles.resource_id) DESC " +
            " LIMIT 0, :countRubric ";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("countRubric", count);
        return namedParameterJdbcTemplate.query(query, parameterSource, this::mapRowToResource);
    }

    @Override
    public Iterable<NewsResource> findAllResources() {
        String query = "SELECT * FROM news_resources " +
            " WHERE news_resources.active = 1 " +
            " AND (news_resources.full_name IS NOT NULL OR news_resources.full_name NOT LIKE '') " +
            " ORDER BY full_name;";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("id", "");
        return namedParameterJdbcTemplate.query(query, parameterSource, this::mapRowToResource);
    }

    @Override
    public Iterable<NewsResource> findByCategories(String resourceListIds) {
        String query = "SELECT * FROM news_resources " +
                " WHERE id IN (" + resourceListIds + ") AND active = 1 " +
                " ORDER BY full_name;";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("resourceListIds", resourceListIds);
        return namedParameterJdbcTemplate.query(query, parameterSource, this::mapRowToResource);
    }

    @Override
    public NewsResource findById(Long id) {
        String query = "SELECT * FROM news_resources WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("id", id);
        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, this::mapRowToResource);
    }

    @Override
    public NewsResource findByResourceKey(String resourceKey) {
        String query = "SELECT * FROM news_resources WHERE resource_key LIKE :resourceKey";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("resourceKey", resourceKey);
        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, this::mapRowToResource);
    }

    @Override
    public boolean checkByResourceKey(String resourceKey) {
        String query = "SELECT * FROM news_resources WHERE resource_key LIKE :resourceKey";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("resourceKey", resourceKey);
        return !namedParameterJdbcTemplate.query(query, parameterSource, this::mapRowToResource).isEmpty();
    }

    private NewsResource mapRowToResource(ResultSet rs, int rowNum) throws SQLException {
        ZonedDateTime dateStamp = ZonedDateTime.of(
                LocalDateTime.parse(rs.getString("date_stamp").substring(0, 19),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Europe/Moscow"));

        NewsResource newsResource = new NewsResource();
        newsResource.setId(rs.getLong("id"));
        newsResource.setResourceKey(rs.getString("resource_key"));
        newsResource.setFullName(rs.getString("full_name"));
        newsResource.setShortName(rs.getString("short_name"));
        newsResource.setResourceLink(rs.getString("resource_link"));
        newsResource.setNewsLink(rs.getString("news_link"));
        newsResource.setActive(rs.getBoolean("active"));
        newsResource.setDateStamp(dateStamp);

        return newsResource;
    }
}
