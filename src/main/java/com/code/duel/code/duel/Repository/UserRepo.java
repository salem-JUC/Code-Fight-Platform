package com.code.duel.code.duel.Repository;

import com.code.duel.code.duel.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Save a new user
    public User save(User user) {
        String sql = "INSERT INTO `user` (username, email, password, role, score) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            ps.setInt(5, user.getScore());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            user.setUserID(key.longValue());
        }
        return user;
    }

    // Find a user by ID
    public User findById(Long userID) {
        String sql = "SELECT * FROM `user` WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{userID}, (rs, rowNum) ->
                new User(
                        rs.getLong("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getInt("score")
                ));
    }

    // Find all users
    public List<User> findAll() {
        String sql = "SELECT * FROM `user`";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new User(
                        rs.getLong("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getInt("score")
                ));
    }

    // Update a user
    public void update(User user) {
        String sql = "UPDATE `user` SET username = ?, email = ?, password = ?, role = ?, score = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getPassword(), user.getRole(), user.getScore(), user.getUserID());
    }

    // Delete a user by ID
    public void deleteById(Long userID) {
        String sql = "DELETE FROM `user` WHERE user_id = ?";
        jdbcTemplate.update(sql, userID);
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM `user` WHERE username = ?";
        return jdbcTemplate.query(sql, new Object[]{username}, (rs, rowNum) ->
                new User(
                        rs.getLong("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getInt("score")
                )).stream().findFirst();
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM `user` WHERE email = ?";
        return jdbcTemplate.query(sql, new Object[]{email}, (rs, rowNum) ->
                new User(
                        rs.getLong("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getInt("score")
                )).stream().findFirst();
    }
}
