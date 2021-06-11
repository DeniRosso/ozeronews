package com.example.ozeronews.repo;

import com.example.ozeronews.models.Rubric;
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
public class RubricRepositoryJDBC implements RubricRepository {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public RubricRepositoryJDBC(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public int[] saveRubric(Rubric rubric) {
        String query = "INSERT INTO rubrics (alias_name, active, date_stamp)" +
                " SELECT :aliasName, :active, :dateStamp FROM DUAL WHERE NOT EXISTS" +
                " (SELECT alias_name FROM rubrics WHERE alias_name LIKE :aliasName) LIMIT 1";
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(rubric);
        return namedParameterJdbcTemplate.batchUpdate(query, batch);
    }

    @Override
    public int[] updateRubric(Rubric rubric) {
        String query = "UPDATE rubrics SET " +
                " rubric_key = :rubricKey, name = :name, active = :active, date_stamp = :dateStamp " +
                " WHERE id = :id ";
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(rubric);
        return namedParameterJdbcTemplate.batchUpdate(query, batch);
    }

    @Override
    public Iterable<Rubric> findAll() {
        String query =
            " SELECT rubrics.id, rubrics.rubric_key, rubrics.alias_name, rubrics.name, rubrics.active, " +
            " rubrics.date_stamp, articles_rubrics.rubric_id, COUNT(articles_rubrics.rubric_id) AS 'rubrics_range' " +
            " FROM articles_rubrics AS articles_rubrics " +
            " LEFT JOIN (SELECT * FROM rubrics ) AS rubrics " +
            " ON (articles_rubrics.rubric_id = rubrics.id) " +
            " WHERE rubrics.alias_name NOT LIKE '' " +
            " GROUP BY articles_rubrics.rubric_id " +
            " ORDER BY COUNT(articles_rubrics.rubric_id) DESC ";
        return namedParameterJdbcTemplate.query(query, this::mapRowToRubricRange);
    }

    @Override
    public Iterable<Rubric> findTopRubrics(int count) {
        String query =
            " SELECT top_rubrics.rubric_key, top_rubrics.name, top_rubrics.active, " +
                " SUM(top_rubrics.rubrics_range) AS 'rubrics_range' " +
            " FROM ( " +
            " SELECT rubrics.id, rubrics.rubric_key, rubrics.alias_name, rubrics.name, rubrics.active, " +
            " rubrics.date_stamp, articles_rubrics.rubric_id, COUNT(articles_rubrics.rubric_id) AS 'rubrics_range' " +
            " FROM articles_rubrics AS articles_rubrics " +
            " LEFT JOIN (SELECT * FROM rubrics ) AS rubrics " +
            " ON (articles_rubrics.rubric_id = rubrics.id) " +
            " WHERE rubrics.active = 1 AND rubrics.alias_name NOT LIKE '' " +
            " AND (rubrics.rubric_key IS NOT NULL OR rubrics.rubric_key NOT LIKE '') " +
            " GROUP BY articles_rubrics.rubric_id " +
            " ) AS top_rubrics" +
            " GROUP BY top_rubrics.rubric_key, top_rubrics.name " +
            " ORDER BY SUM(top_rubrics.rubrics_range) DESC " +
            " LIMIT 0, :countRubric; ";
        SqlParameterSource rubric = new MapSqlParameterSource().addValue("countRubric", count);
        return namedParameterJdbcTemplate.query(query, rubric, this::mapRowToRubricTop);
    }

    @Override
    public Iterable<Rubric> findAllRubrics() {
        String query = "SELECT rubric_key, name FROM rubrics " +
                " WHERE active = 1 " +
                " AND (rubric_key IS NOT NULL OR rubric_key NOT LIKE '') " +
                " GROUP BY rubric_key, name " +
                " ORDER BY name; ";
        SqlParameterSource rubric = new MapSqlParameterSource().addValue("active", true);
        return namedParameterJdbcTemplate.query(query, rubric, this::mapRowToRubricAll);
    }

    @Override
    public Rubric findById(Long id) {
        String query = "SELECT * FROM rubrics WHERE id = :id";
        SqlParameterSource rubric = new MapSqlParameterSource().addValue("id", id);
        return namedParameterJdbcTemplate.queryForObject(query, rubric, this::mapRowToRubric);
    }

    @Override
    public Iterable<Rubric> findByRubricKey(String rubricKey) {
        String query = "SELECT * FROM rubrics WHERE rubric_key LIKE :rubricKey";
        SqlParameterSource rubric = new MapSqlParameterSource().addValue("rubricKey", rubricKey);
        return namedParameterJdbcTemplate.query(query, rubric, this::mapRowToRubric);
    }

    @Override
    public Rubric findByRubricAliasName(String rubricAliasName) {
        String query = "SELECT * FROM rubrics WHERE LOWER(alias_name) LIKE :rubricAliasName";
        SqlParameterSource rubric = new MapSqlParameterSource().addValue("rubricAliasName", rubricAliasName.toLowerCase());
        return namedParameterJdbcTemplate.queryForObject(query, rubric, this::mapRowToRubric);
    }

    private Rubric mapRowToRubric(ResultSet rs, int rowNum) throws SQLException {
        ZonedDateTime dateStamp = ZonedDateTime.of(
                LocalDateTime.parse(rs.getString("date_stamp"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")),
                ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Europe/Moscow"));

        Rubric rubric = new Rubric();
        rubric.setId(rs.getLong("id"));
        rubric.setRubricKey(rs.getString("rubric_key"));
        rubric.setName(rs.getString("name"));
        rubric.setAliasName(rs.getString("alias_name"));
        rubric.setActive(rs.getBoolean("active"));
        rubric.setDateStamp(dateStamp);
        return rubric;
    }

    private Rubric mapRowToRubricRange(ResultSet rs, int rowNum) throws SQLException {
        ZonedDateTime dateStamp = ZonedDateTime.of(
                LocalDateTime.parse(rs.getString("date_stamp").substring(0, 19),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Europe/Moscow"));
        Rubric rubric = new Rubric();
        rubric.setId(rs.getLong("id"));
        rubric.setRubricKey(rs.getString("rubric_key"));
        rubric.setName(rs.getString("name"));
        rubric.setAliasName(rs.getString("alias_name"));
        rubric.setRange(rs.getString("rubrics_range"));
        rubric.setActive(rs.getBoolean("active"));
        rubric.setDateStamp(dateStamp);
        return rubric;
    }

    private Rubric mapRowToRubricTop(ResultSet rs, int rowNum) throws SQLException {
        Rubric rubric = new Rubric();
        rubric.setRubricKey(rs.getString("rubric_key"));
        rubric.setName(rs.getString("name"));
        rubric.setRange(rs.getString("rubrics_range"));
        rubric.setActive(rs.getBoolean("active"));
        return rubric;
    }

    private Rubric mapRowToRubricAll(ResultSet rs, int rowNum) throws SQLException {
        Rubric rubric = new Rubric();
        rubric.setRubricKey(rs.getString("rubric_key"));
        rubric.setName(rs.getString("name"));
        return rubric;
    }
}
