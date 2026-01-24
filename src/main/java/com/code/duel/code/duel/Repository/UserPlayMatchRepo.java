package com.code.duel.code.duel.Repository;


import com.code.duel.code.duel.Model.UserPlayMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserPlayMatchRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Save a new user_play_match entry
    public void save(UserPlayMatch userPlayMatch) {
        String sql = "INSERT INTO user_play_match (user_id, match_id, username, user_score) VALUES (?, ? , ?, ?)";
        jdbcTemplate.update(sql, userPlayMatch.getUserID(), userPlayMatch.getMatchID(),userPlayMatch.getUsername(), userPlayMatch.getUserScore());
    }

    // Find all entries for a specific user
    public List<UserPlayMatch> findByUserId(Long userID) {
        String sql = "SELECT * FROM user_play_match WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userID}, (rs, rowNum) ->
                new UserPlayMatch(
                        rs.getLong("user_id"),
                        rs.getLong("match_id"),
                        rs.getString("username"),
                        rs.getInt("user_score")
                ));
    }

    // Find all entries for a specific match
    public List<UserPlayMatch> findByMatchId(Long matchID) {
        String sql = "SELECT * FROM user_play_match WHERE match_id = ?";
        return jdbcTemplate.query(sql, new Object[]{matchID}, (rs, rowNum) ->
                new UserPlayMatch(
                        rs.getLong("user_id"),
                        rs.getLong("match_id"),
                        rs.getString("username"),
                        rs.getInt("user_score")
                ));
    }

    // Update a user_play_match entry
    public void update(UserPlayMatch userPlayMatch) {
        String sql = "UPDATE user_play_match SET user_score = ? WHERE user_id = ? AND match_id = ?";
        jdbcTemplate.update(sql, userPlayMatch.getUserScore(), userPlayMatch.getUserID() ,userPlayMatch.getMatchID());
    }

    // Delete a user_play_match entry
    public void delete(Long userID, Long matchID) {
        String sql = "DELETE FROM user_play_match WHERE user_id = ? AND match_id = ?";
        jdbcTemplate.update(sql, userID, matchID);
    }

    public UserPlayMatch findByUserIDAndMatchID(Long playerId, Long matchId) {
        String sql = "SELECT * FROM user_play_match WHERE user_id = ? AND match_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{playerId, matchId}, (rs, rowNum) ->
                new UserPlayMatch(
                        rs.getLong("user_id"),
                        rs.getLong("match_id"),
                        rs.getString("username"),
                        rs.getInt("user_score")
                ));
    }

    public UserPlayMatch findTheOpponent(Long playerId, Long matchId) {
        String sql = "SELECT * FROM user_play_match WHERE user_id != ? AND match_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{playerId, matchId}, (rs, rowNum) ->
                new UserPlayMatch(
                        rs.getLong("user_id"),
                        rs.getLong("match_id"),
                        rs.getString("username"),
                        rs.getInt("user_score")
                ));
    }
}
