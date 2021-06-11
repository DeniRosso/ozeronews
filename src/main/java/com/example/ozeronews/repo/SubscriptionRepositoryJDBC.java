package com.example.ozeronews.repo;

import com.example.ozeronews.models.NewsResource;
import com.example.ozeronews.models.Subscription;
import com.example.ozeronews.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SubscriptionRepositoryJDBC implements SubscriptionRepository {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private UserRepository userRepository;
    private NewsResourceRepository newsResourceRepository;

    @Autowired
    public SubscriptionRepositoryJDBC(JdbcTemplate jdbcTemplate,
                                      NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                      UserRepository userRepository,
                                      NewsResourceRepository newsResourceRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.userRepository = userRepository;
        this.newsResourceRepository = newsResourceRepository;
    }

    @Override
    public int[] saveSubscription(Subscription subscription) {
        String query = "INSERT INTO subscriptions (user_id, resource_id, active, date_stamp) VALUE (?, ?, ?, ?) ";
        jdbcTemplate.update(query,
                subscription.getUserId().getId(),
                subscription.getResourceId().getId(),
                subscription.isActive(),
                subscription.getDateStamp()
        );
        return new int[1];
    }

    @Override
    public int[] updateSubscription(Subscription subscription) {
        String query = "UPDATE subscriptions SET active = ?, date_stamp = ? WHERE user_id = ? AND  resource_id = ?";
        jdbcTemplate.update(query,
                subscription.isActive(),
                subscription.getDateStamp(),
                subscription.getUserId().getId(),
                subscription.getResourceId().getId()
        );
        return new int[1];
    }

    @Override
    public boolean checkBySubscription(Subscription subscription) {
        String query = "SELECT * FROM subscriptions WHERE user_id = :userId AND resource_id = :resourceId";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("userId", subscription.getUserId().getId())
                .addValue("resourceId", subscription.getResourceId().getId());
        return !namedParameterJdbcTemplate.query(query, parameterSource, this::mapRowToSubscriptions).isEmpty();
    }

    @Override
    public boolean checkByEmail(User user) {
        String query = "SELECT * FROM subscriptions WHERE user_id = :userId";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("userId", user.getId());
        return !namedParameterJdbcTemplate.query(query, parameterSource, this::mapRowToSubscriptions).isEmpty();
    }

    @Override
    public Iterable<Subscription> findBySubscription(Subscription subscription) {
        String query = "SELECT * FROM subscriptions WHERE user_id = :userId AND resource_id = :resourceId";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("userId", subscription.getUserId())
                .addValue("resourceId", subscription.getResourceId());
        return namedParameterJdbcTemplate.query(query, parameterSource, this::mapRowToSubscriptions);
    }

    @Override
    public Iterable<Subscription> findByUser(User user) {
        String query = "SELECT * FROM subscriptions WHERE user_id = :userId";
        SqlParameterSource subscription = new MapSqlParameterSource().addValue("userId", user.getId());
        return namedParameterJdbcTemplate.query(query, subscription, this::mapRowToSubscriptions);
    }

    @Override
    public Iterable<Subscription> findByUserNewsResources(User user, Iterable<NewsResource> newsResources) {
        return null;
    }

    private Subscription mapRowToSubscriptions(ResultSet rs, int rowNum) throws SQLException {
        ZonedDateTime dateStamp = ZonedDateTime.of(
                LocalDateTime.parse(rs.getString("date_stamp").substring(0, 19),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Europe/Moscow"));

        Subscription subscription = new Subscription();
        subscription.setId(rs.getLong("id"));
        subscription.setUserId(userRepository.findById(rs.getLong("user_id")));
        subscription.setResourceId(newsResourceRepository.findById(rs.getLong("resource_id")));
        subscription.setActive(rs.getBoolean("active"));
        subscription.setDateStamp(dateStamp); //result.getBoolean("status_follow") ? true : false

        return subscription;
    }
}
