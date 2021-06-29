package com.example.ozeronews.repo;

import com.example.ozeronews.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
public class UserRepositoryJDBC implements UserRepository{

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private RoleRepository roleRepository;
    private AuthProviderRepository authProviderRepository;

    @Autowired
    public UserRepositoryJDBC(JdbcTemplate jdbcTemplate,
                              NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                              RoleRepository roleRepository,
                              AuthProviderRepository authProviderRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.roleRepository = roleRepository;
        this.authProviderRepository = authProviderRepository;
    }

    @Override
    public int[] save(User user) {
        String query = "INSERT INTO users (" +
                " username, email, password, firstname, lastname, avatar, activation_code, recovery_code, " +
                " active, date_stamp) " +
                " VALUE(:username, :email, :password, :firstname, :lastname, :avatar, :activationCode, :recoveryCode," +
                " :active, :dateStamp)";
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(user);
        return namedParameterJdbcTemplate.batchUpdate(query, batch);
    }

    @Override
    public int[] updateUser(User user) {
        String query = "UPDATE news_resources SET " +
                " username = :username, email = :email, firstname = :firstname, lastname = :lastname, " +
                " password = :password, avatar = :avatar, activation_code = :activationCode, " +
                " recovery_code = :recoveryCode, role = :role, active = :active, date_stamp = :dateStamp " +
                " WHERE id = :id ";
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(user);
        return namedParameterJdbcTemplate.batchUpdate(query, batch);
    }

    @Override
    public int[] updatePassword(User user) {
        String query = "UPDATE users SET password = :password, recovery_code = :recoveryCode, " +
                " active = :active, date_stamp = :dateStamp " +
                " WHERE id = :id ";
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(user);
        return namedParameterJdbcTemplate.batchUpdate(query, batch);
    }

    @Override
    public int[] updateActive(User user) {
        String query = "UPDATE users SET active = :active WHERE id = :id ";
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(user);
        return namedParameterJdbcTemplate.batchUpdate(query, batch);
    }

    @Override
    public int[] saveRecoveryCode(User user) {
        String query = "UPDATE users SET recovery_code = :recoveryCode WHERE id = :id";
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(user);
        return namedParameterJdbcTemplate.batchUpdate(query, batch);
    }

    @Override
    public Iterable<User> findAll() {
        String query = "SELECT * FROM users";
        return namedParameterJdbcTemplate.query(query, this::mapRowToUser);
    }

    @Override
    public User findById(Long id) {
        String query = "SELECT * FROM users WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("id", id);
        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, this::mapRowToUser);
    }

    @Override
    public User findByEmail(String email) {
        String query = "SELECT * FROM users WHERE email LIKE :email";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("email", email);
        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, this::mapRowToUser);
    }

    @Override
    public User findByAuthorization(User user) {
        if (user != null) {
            String query = "SELECT * FROM users WHERE email LIKE :email";
            SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("email", user.getEmail());
            return namedParameterJdbcTemplate.queryForObject(query, parameterSource, this::mapRowToUser);
        }
        return new User();
    }

    @Override
    public User findByUsername(String username) {
        String query = "SELECT * FROM users WHERE username LIKE :username";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("username", username);
        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, this::mapRowToUser);
    }

    @Override
    public Iterable<User> findByActivationCode(String activationCode) {
        String query = "SELECT * FROM users WHERE activation_code LIKE :activationCode";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("activationCode", activationCode);
        return namedParameterJdbcTemplate.query(query, parameterSource, this::mapRowToUser);
    }

    @Override
    public List<User> findByRecoveryCode(String recoveryCode) {
        String query = "SELECT * FROM users WHERE recovery_code LIKE :recoveryCode";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("recoveryCode", recoveryCode);
        return namedParameterJdbcTemplate.query(query, parameterSource, this::mapRowToUser);
    }

    @Override
    public boolean checkByEmail(String email) {
        String query = "SELECT * FROM users WHERE email LIKE :email";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("email", email);
        return !namedParameterJdbcTemplate.query(query, parameterSource, this::mapRowToUser).isEmpty();
    }

    @Override
    public boolean checkByUsername(String username) {
        String query = "SELECT * FROM users WHERE username LIKE :username";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("username", username);
        return !namedParameterJdbcTemplate.query(query, parameterSource, this::mapRowToUser).isEmpty();
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        ZonedDateTime dateStamp = ZonedDateTime.of(
                LocalDateTime.parse(rs.getString("date_stamp").substring(0, 19),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                ), ZoneId.of("UTC"));

        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setFirstname(rs.getString("firstname"));
        user.setLastname(rs.getString("lastname"));
        user.setActivationCode(rs.getString("activation_code"));
        user.setRecoveryCode(rs.getString("recovery_code"));
        user.setAvatar(rs.getString("avatar"));
        user.setActive(rs.getBoolean("active"));
        user.setRole(Collections.singleton(roleRepository.findById(rs.getLong("id"))));
        user.setAuthProvider(Collections.singleton(authProviderRepository.findById(rs.getLong("id"))));
        user.setDateStamp(dateStamp);
        return user;
    }
}
