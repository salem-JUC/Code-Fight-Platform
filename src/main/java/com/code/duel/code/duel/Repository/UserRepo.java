package com.code.duel.code.duel.Repository;

import com.code.duel.code.duel.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Save a new user
    public void save(User user) {
        String sql = "INSERT INTO `user` (userID, Username, Email, Password, Role, Score) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getUserID(), user.getUsername(), user.getEmail(), user.getPassword(), user.getRole(), user.getScore());
    }

    // Find a user by ID
    public User findById(Long userID) {
        String sql = "SELECT * FROM `user` WHERE userID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{userID}, (rs, rowNum) ->
                new User(
                        rs.getLong("userID"),
                        rs.getString("Username"),
                        rs.getString("Email"),
                        rs.getString("Password"),
                        rs.getString("Role"),
                        rs.getInt("Score")
                ));
    }

    // Find all users
    public List<User> findAll() {
        String sql = "SELECT * FROM `user`";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new User(
                        rs.getLong("userID"),
                        rs.getString("Username"),
                        rs.getString("Email"),
                        rs.getString("Password"),
                        rs.getString("Role"),
                        rs.getInt("Score")
                ));
    }

    // Update a user
    public void update(User user) {
        String sql = "UPDATE `user` SET Username = ?, Email = ?, Password = ?, Role = ?, Score = ? WHERE userID = ?";
        jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getPassword(), user.getRole(), user.getScore(), user.getUserID());
    }

    // Delete a user by ID
    public void deleteById(Long userID) {
        String sql = "DELETE FROM `user` WHERE userID = ?";
        jdbcTemplate.update(sql, userID);
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM `user` WHERE Username = ?";
        return jdbcTemplate.query(sql, new Object[]{username}, (rs, rowNum) ->
                new User(
                        rs.getLong("userID"),
                        rs.getString("Username"),
                        rs.getString("Email"),
                        rs.getString("Password"),
                        rs.getString("Role"),
                        rs.getInt("Score")
                )).stream().findFirst();
    }
}
