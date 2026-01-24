package com.code.duel.code.duel.Repository;

import com.code.duel.code.duel.Model.Challenge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ChallengeRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Save a new challenge
    public void save(Challenge challenge) {
        String sql = "INSERT INTO challenge (title, description, difficulty, sample) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, challenge.getTitle());
            ps.setString(2, challenge.getDescription());
            ps.setString(3, challenge.getDifficulty());
            ps.setString(4, challenge.getSample());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            challenge.setChallengeID(key.longValue());
        }
    }

    // Find a challenge by ID
    public Challenge findById(Long challengeID) {
        String sql = "SELECT * FROM challenge WHERE challenge_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{challengeID}, (rs, rowNum) ->
                new Challenge(
                        rs.getLong("challenge_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("difficulty"),
                        rs.getString("sample")
                ));
    }

    // Find all challenges
    public List<Challenge> findAll() {
        String sql = "SELECT * FROM challenge";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Challenge(
                        rs.getLong("challenge_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("difficulty"),
                        rs.getString("sample")
                ));
    }

    // Update a challenge
    public void update(Challenge challenge) {
        String sql = "UPDATE challenge SET title = ?, description = ?, difficulty = ?, sample = ? WHERE challenge_id = ?";
        jdbcTemplate.update(sql, challenge.getTitle(), challenge.getDescription(), challenge.getDifficulty(), challenge.getSample(), challenge.getChallengeID());
    }

    // Delete a challenge by ID
    public void deleteById(Long challengeID) {
        String sql = "DELETE FROM challenge WHERE challenge_id = ?";
        jdbcTemplate.update(sql, challengeID);
    }

    public Optional<Challenge> findRandom(){
        String sql = """
                SELECT * FROM challenge
                WHERE challenge_id >= (
                    SELECT FLOOR(RAND() * (SELECT MAX(challenge_id) FROM challenge))
                )
                ORDER BY challenge_id
                LIMIT 1;
                """;
        return Optional.of(jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                new Challenge(
                        rs.getLong("challenge_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("difficulty"),
                        rs.getString("sample")
                )));
    }

    public Challenge findRandomWithDifficulty(String difficulty) {
        String sql = "SELECT * FROM challenge WHERE difficulty = ? ORDER BY RAND() LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    new Object[]{difficulty},
                    (rs, rowNum) -> new Challenge(
                            rs.getLong("challenge_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("difficulty"),
                            rs.getString("sample")
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return null; // No matching challenge found
        }
    }
}
