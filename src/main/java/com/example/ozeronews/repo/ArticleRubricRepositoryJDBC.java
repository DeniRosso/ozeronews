package com.example.ozeronews.repo;

import com.example.ozeronews.models.Article;
import com.example.ozeronews.models.ArticleRubric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ArticleRubricRepositoryJDBC implements ArticleRubricRepository{

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private RubricRepository rubricRepository;

    @Autowired
    public ArticleRubricRepositoryJDBC(JdbcTemplate jdbcTemplate,
                                       NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                       RubricRepository rubricRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.rubricRepository = rubricRepository;
    }

    @Override
    public int[] saveArticleRubric(Article article) {
        String query = "INSERT INTO articles_rubrics (article_id, rubric_id) VALUE(?, ?) " +
                " ON DUPLICATE KEY UPDATE article_id = ? AND rubric_id = ?";
        for (int i = 0; i < article.getArticleRubric().size(); i++) {
            jdbcTemplate.update(query,
                article.getId(),
                article.getArticleRubric().get(i).getRubricId().getId(),
                article.getId(),
                article.getArticleRubric().get(i).getRubricId().getId()
            );
        }
        return new int[1];
    }

    @Override
    public int delete(String listIds) {
        String query = "DELETE FROM articles_rubrics WHERE article_id IN (" + listIds  + ")";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("listIds", listIds);
        return namedParameterJdbcTemplate.update(query, parameterSource);
    }

    @Override
    public List<ArticleRubric> findByArticleId(String articleId) {
        String query = "SELECT * FROM articles_rubrics WHERE article_id = :articleId";
        SqlParameterSource article = new MapSqlParameterSource().addValue("articleId", articleId);
        return namedParameterJdbcTemplate.query(query, article, this::mapRowToArticleRubric);
    }

    @Override
    public List<ArticleRubric> findByRubricId(String rubricId) {
        String query = "SELECT * FROM articles_rubrics WHERE rubric_id = :rubricId";
        SqlParameterSource article = new MapSqlParameterSource().addValue("rubricId", rubricId);
        return namedParameterJdbcTemplate.query(query, article, this::mapRowToArticleRubric);
    }

    private ArticleRubric mapRowToArticleRubric(ResultSet rs, int rowNum) throws SQLException {

        ArticleRubric articleRubric = new ArticleRubric(
                rs.getLong("id"),
                new Article(),
                rubricRepository.findById(Long.valueOf(rs.getString("rubric_id")))
        );

        return articleRubric;
    }
}
