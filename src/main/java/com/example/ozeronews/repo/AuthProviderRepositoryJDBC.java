package com.example.ozeronews.repo;

import com.example.ozeronews.models.AuthProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class AuthProviderRepositoryJDBC implements AuthProviderRepository {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public AuthProviderRepositoryJDBC(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Iterable<AuthProvider> findAll() {
        String query = "SELECT * FROM auth_providers";
        return namedParameterJdbcTemplate.query(query, this::mapRowToAuthProvider);
    }

    @Override
    public AuthProvider findById(Long id) {
        String query = "SELECT * FROM auth_providers WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("id", id);
        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, this::mapRowToAuthProvider);
    }

    private AuthProvider mapRowToAuthProvider(ResultSet rs, int rowNum) throws SQLException {
        return AuthProvider.valueOf(rs.getString("auth_provider"));
    }
}
