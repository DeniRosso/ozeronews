package com.example.ozeronews.repo;

import com.example.ozeronews.models.Role;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class RoleRepositoryJDBC implements RoleRepository {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public RoleRepositoryJDBC(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Iterable<Role> findAll() {
        String query = "SELECT * FROM users_roles";
        return namedParameterJdbcTemplate.query(query, this::mapRowToRole);
    }

    @Override
    public Role findById(Long id) {
        String query = "SELECT * FROM users_roles WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("id", id);
        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, this::mapRowToRole);
    }

    private Role mapRowToRole(ResultSet rs, int rowNum) throws SQLException {
        return Role.valueOf(rs.getString("role"));
    }
}
