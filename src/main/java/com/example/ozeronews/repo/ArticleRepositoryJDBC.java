package com.example.ozeronews.repo;

import com.example.ozeronews.models.Article;
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
public class ArticleRepositoryJDBC implements ArticleRepository {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private NewsResourceRepository newsResourcesRepository;
    private ArticleRubricRepository articleRubricRepository;
    private RubricRepository rubricRepository;

    @Autowired
    public ArticleRepositoryJDBC(JdbcTemplate jdbcTemplate,
                                 NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                 NewsResourceRepository newsResourcesRepository,
                                 ArticleRubricRepository articleRubricRepository,
                                 RubricRepository rubricRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.newsResourcesRepository = newsResourcesRepository;
        this.articleRubricRepository = articleRubricRepository;
        this.rubricRepository = rubricRepository;
    }

    @Override
    public int[] saveArticle(Article article) {
        String query = "INSERT INTO articles " +
                " (resource_id, number, title, link, image, date_publication, date_stamp)" +
                " VALUE(?, ?, ?, ?, ?, ?, ?) " +
                " ON DUPLICATE KEY UPDATE number = ?";
        jdbcTemplate.update(query,
                article.getResourceId().getId(),
                article.getNumber(),
                article.getTitle(),
                article.getLink(),
                article.getImage(),
                article.getDatePublication(),
                article.getDateStamp(),
                article.getNumber()
                );
        return new int[1];
    }

    @Override
    public int delete(ZonedDateTime date) {
        String query = "DELETE FROM articles WHERE date_publication < :date";
//        String query = "SELECT * FROM articles WHERE date_publication < :date ORDER BY date_publication DESC";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("date", date);
        int articles = namedParameterJdbcTemplate.update(query, parameterSource);

        System.out.println("articles = " + articles);
        return articles;
    }

    @Override
    public int delete(String listIds) {
        String query = "DELETE FROM articles WHERE id IN (" + listIds  + ")";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("listIds", listIds);
        return namedParameterJdbcTemplate.update(query, parameterSource);
    }

    @Override
    public Iterable<Article> findArticleById(Long id, int startNumber, int linesNumber) {
        String query = "SELECT * FROM articles WHERE id = :id" +
            " ORDER BY date_publication DESC, number DESC LIMIT :startNumber, :linesNumber";
        SqlParameterSource rowLimit = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("startNumber", startNumber)
                .addValue("linesNumber", linesNumber);
        return namedParameterJdbcTemplate.query(query, rowLimit, this::mapRowToArticle);
    }

    @Override
    public Iterable<Article> findAllArticlesByLimit(int startNumber, int linesNumber) {
        String query =
            "SELECT * FROM articles ORDER BY date_publication DESC, number DESC LIMIT :startNumber, :linesNumber";
        SqlParameterSource rowLimit = new MapSqlParameterSource()
                .addValue("startNumber", startNumber)
                .addValue("linesNumber", linesNumber);
        return namedParameterJdbcTemplate.query(query, rowLimit, this::mapRowToArticle);
    }

    @Override
    public Iterable<Article> findArticlesByResource(Long resourceId, int startNumber, int linesNumber) {
        String query =
            "SELECT * FROM articles WHERE resource_id = :resourceId " +
            " ORDER BY date_publication DESC, number DESC LIMIT :startNumber, :linesNumber";
        SqlParameterSource rowLimit = new MapSqlParameterSource()
                .addValue("startNumber", startNumber)
                .addValue("linesNumber", linesNumber)
                .addValue("resourceId", resourceId);
        return namedParameterJdbcTemplate.query(query, rowLimit, this::mapRowToArticle);
    }

    @Override
    public Iterable<Article> findArticlesByRubric(String rubricsListIds, int startNumber, int linesNumber) {
        String query =
            "SELECT * FROM articles, articles_rubrics " +
            " WHERE articles_rubrics.rubric_id IN ( " + rubricsListIds + " )" +
            " AND articles.id = articles_rubrics.article_id " +
            " ORDER BY date_publication DESC, number DESC " +
            " LIMIT :startNumber, :linesNumber";
        SqlParameterSource rowLimit = new MapSqlParameterSource()
                .addValue("startNumber", startNumber)
                .addValue("linesNumber", linesNumber)
                .addValue("rubricsListIds", rubricsListIds);
        return namedParameterJdbcTemplate.query(query, rowLimit, this::mapRowToArticle);
    }

    @Override
    public Iterable<Article> findByDate(ZonedDateTime date) {
        String query = "SELECT * FROM articles WHERE date_publication < :date ORDER BY date_publication DESC";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("date", date);
        return namedParameterJdbcTemplate.query(query, parameterSource, this::mapRowToArticleOnly);
    }

    @Override
    public Iterable<Article> findArticlesBySearch(String search, int startNumber, int linesNumber) {
        StringBuilder search1 = new StringBuilder();
        search1 = search1.append("%").append(search).append("%");
        search = String.valueOf(search1);
        String query = "SELECT * FROM articles WHERE title LIKE :search " +
            " ORDER BY date_publication DESC, number DESC LIMIT :startNumber, :linesNumber";
        SqlParameterSource rowLimit = new MapSqlParameterSource()
                .addValue("startNumber", startNumber)
                .addValue("linesNumber", linesNumber)
                .addValue("search", search);
        return namedParameterJdbcTemplate.query(query, rowLimit, this::mapRowToArticle);
    }

    @Override
    public Iterable<Article> findArticlesBySubscriptions(String subscriptionsListId, int startNumber, int linesNumber) {
        String query = "SELECT * FROM articles WHERE resource_id IN (" + subscriptionsListId + ") " +
                " ORDER BY date_publication DESC, number DESC LIMIT :startNumber, :linesNumber";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("startNumber", startNumber)
                .addValue("linesNumber", linesNumber)
                .addValue("subscriptionsListId", subscriptionsListId);
        return namedParameterJdbcTemplate.query(query, param, this::mapRowToArticle);
    }

    @Override
    public Iterable<Article> findSubscriptionsArticlesBySearch(String search, String subscriptionsListId, int startNumber, int linesNumber) {
        StringBuilder search1 = new StringBuilder();
        search1 = search1.append("%").append(search).append("%");
        search = String.valueOf(search1);
        String query = "SELECT * FROM articles WHERE resource_id IN (" + subscriptionsListId + ") " +
                " AND title LIKE :search " +
                " ORDER BY date_publication DESC, number DESC LIMIT :startNumber, :linesNumber";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("startNumber", startNumber)
                .addValue("linesNumber", linesNumber)
                .addValue("search", search)
                .addValue("subscriptionsListId", subscriptionsListId);
        return namedParameterJdbcTemplate.query(query, param, this::mapRowToArticle);
    }

    @Override
    public Article findByMaxId() {
        String query = "SELECT * FROM articles WHERE id = (SELECT MAX(id) FROM articles)";
        return jdbcTemplate.queryForObject(query, this::mapRowToArticle);
    }

    @Override
    public boolean checkByArticleNumber(String articleNumber) {
        String query = "SELECT * FROM articles WHERE number LIKE :articleNumber";
        SqlParameterSource article = new MapSqlParameterSource().addValue("articleNumber", articleNumber);
        return !namedParameterJdbcTemplate.query(query, article, this::mapRowToArticle).isEmpty();
    }

    private Article mapRowToArticle(ResultSet rs, int rowNum) throws SQLException {
        ZonedDateTime datePublication = ZonedDateTime.of(
                LocalDateTime.parse(rs.getString("date_publication").substring(0, 19),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), //yyyy-MM-dd HH:mm:ss.SSS Z
                ZoneId.of("UTC")); //ZoneId.systemDefault();

        ZonedDateTime dateStamp = ZonedDateTime.of(
                LocalDateTime.parse(rs.getString("date_stamp").substring(0, 19),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                ZoneId.of("UTC"));

        Article article = new Article();
        article.setId(rs.getLong("id"));
        article.setNumber(rs.getString("number"));
        article.setTitle(rs.getString("title"));
        article.setLink(rs.getString("link"));
        article.setImage(rs.getString("image"));
        article.setDatePublication(datePublication);
        article.setDateStamp(dateStamp);
        article.setResourceId(newsResourcesRepository.findById(rs.getLong("resource_id")));
        article.setArticleRubric(articleRubricRepository.findByArticleId(rs.getString("id")));

        return article;
    }

    private Article mapRowToArticleOnly(ResultSet rs, int rowNum) throws SQLException {
        ZonedDateTime datePublication = ZonedDateTime.of(
                LocalDateTime.parse(rs.getString("date_publication").substring(0, 19),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), //yyyy-MM-dd HH:mm:ss.SSS Z
                ZoneId.of("UTC")); //ZoneId.systemDefault();

        ZonedDateTime dateStamp = ZonedDateTime.of(
                LocalDateTime.parse(rs.getString("date_stamp").substring(0, 19),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                ZoneId.of("UTC"));

        Article article = new Article();
        article.setId(rs.getLong("id"));
        article.setNumber(rs.getString("number"));
        article.setTitle(rs.getString("title"));
        article.setLink(rs.getString("link"));
        article.setImage(rs.getString("image"));
        article.setDatePublication(datePublication);
        article.setDateStamp(dateStamp);

        return article;
    }
}
