package com.code.duel.code.duel.Repository;


import com.code.duel.code.duel.DTO.MatchDTO.MatchWithPlayersDTO;
import com.code.duel.code.duel.Mappers.ResponseMapper.MatchStatusResponseMapper;
import com.code.duel.code.duel.Model.Difficulty;
import com.code.duel.code.duel.Model.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;


@Repository
public class MatchRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Save a new match
    public Match save(Match match) {
        String sql = "INSERT INTO `match` (challenge_id, difficulty, programming_language, status, winner_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, match.getCurrentChallengeId());
            ps.setString(2, match.getDifficulty());
            ps.setString(3, match.getProgrammingLanguage());
            ps.setString(4, match.getStatus());
            ps.setObject(5, match.getWinnerId());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            match.setMatchID(key.longValue());
        }
        return match;
    }

    // Find a match by ID
    public Match findById(Long matchID) {
        String sql = "SELECT * FROM `match` WHERE match_id = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{matchID}, (rs, rowNum) ->
                new Match(
                        rs.getLong("match_id"),
                        rs.getLong("challenge_id"),
                        rs.getString("difficulty"),
                        rs.getString("programming_language"),
                        rs.getString("status"),
                        rs.getLong("winner_id")
                ));

    }

    // Find all matches
    public List<Match> findAll() {
        String sql = "SELECT * FROM `match`";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Match(
                        rs.getLong("match_id"),
                        rs.getLong("challenge_id"),
                        rs.getString("difficulty"),
                        rs.getString("programming_language"),
                        rs.getString("status"),
                        rs.getLong("winner_id")
                ));
    }

    public List<MatchWithPlayersDTO> findAllWithPlayers() {
        String sql = """
            SELECT m.*, GROUP_CONCAT(upm.username SEPARATOR ', ') as players
            FROM `match` m
            LEFT JOIN user_play_match upm ON m.match_id = upm.match_id
            GROUP BY m.match_id
            ORDER BY m.match_id DESC
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new MatchWithPlayersDTO(
                        rs.getLong("match_id"),
                        rs.getLong("challenge_id"),
                        rs.getString("difficulty"),
                        rs.getString("programming_language"),
                        rs.getString("status"),
                        rs.getLong("winner_id"),
                        rs.getString("players")
                ));
    }

    // Update a match (including challenge ID)
    public void update(Match match) {
        String sql = "UPDATE `match` SET status = ?, challenge_id = ?, winner_id = ? WHERE match_id = ?";
        jdbcTemplate.update(sql,
                match.getStatus(),
                match.getCurrentChallengeId(),
                match.getWinnerId(),
                match.getMatchID());
    }


    // Update only the current challenge ID
    public void updateChallenge(Long matchId, Long challengeId) {
        String sql = "UPDATE `match` SET challenge_id = ? WHERE match_id = ?";
        jdbcTemplate.update(sql, challengeId, matchId);
    }

    // Delete a match by ID
    public void deleteById(Long matchID) {
        String sql = "DELETE FROM `match` WHERE match_id = ?";
        jdbcTemplate.update(sql, matchID);
    }

    // Find matches by status
    public List<Match> findByStatus(String status) {
        String sql = "SELECT * FROM `match` WHERE status = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Match(
                        rs.getLong("match_id"),
                        rs.getLong("challenge_id"),
                        rs.getString("difficulty"),
                        rs.getString("programming_language"),
                        rs.getString("status"),
                        rs.getLong("winner_id")
                ));
    }

    // Find first pending match
    public Optional<Match> findFirstPending() {
        String sql = "SELECT * FROM `match` WHERE status = 'PENDING' ORDER BY match_id ASC LIMIT 1";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Match(
                        rs.getLong("match_id"),
                        rs.getLong("challenge_id"),
                        rs.getString("difficulty"),
                        rs.getString("programming_language"),
                        rs.getString("status"),
                        rs.getLong("winner_id")
                )).stream().findFirst();
    }

    public List<Match> findAllMatchesByUserIdOrderByRecent(Long userId) {
        String sql = """
            SELECT m.match_id, m.challenge_id, m.difficulty, 
                   m.programming_language, m.status, m.winner_id
            FROM `match` m
            JOIN user_play_match upm ON m.match_id = upm.match_id
            WHERE upm.user_id = ?
            ORDER BY m.match_id DESC
            """;

        return jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) -> {
            Match match = new Match();
            match.setMatchID(rs.getLong("match_id"));
            match.setCurrentChallengeId(rs.getLong("challenge_id"));
            match.setDifficulty(rs.getString("difficulty"));
            match.setProgrammingLanguage(rs.getString("programming_language"));
            match.setStatus(rs.getString("status"));
            match.setWinnerId(rs.getLong("winner_id"));
            return match;
        });
    }

    public List<Match> findAllMatchesWinnedByUserIdOrderByRecent(Long userId) {
        String sql = """
            SELECT m.match_id, m.challenge_id, m.difficulty, 
                   m.programming_language, m.status, m.winner_id
            FROM `match` m
            WHERE m.winner_id = ?
            ORDER BY m.match_id DESC
            """;

        return jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) -> {
            Match match = new Match();
            match.setMatchID(rs.getLong("match_id"));
            match.setCurrentChallengeId(rs.getLong("challenge_id"));
            match.setDifficulty(rs.getString("difficulty"));
            match.setProgrammingLanguage(rs.getString("programming_language"));
            match.setStatus(rs.getString("status"));
            match.setWinnerId(rs.getLong("winner_id"));
            return match;
        });
    }

    public MatchStatusResponseMapper queryMatchStatus(Long matchId, Long playerId) {
        String sql = """
            SELECT m.difficulty,
                   m.programming_language,
                   c.title,
                   c.description,
                   c.sample,
                   fupm.username AS player_name,
                   fupm.user_score AS player_score,
                   fupm.user_id AS player_id,
                   supm.username AS second_name,
                   supm.user_score AS second_score,
                   supm.user_id AS second_id
            FROM `match` AS m
            JOIN challenge AS c ON c.challenge_id = m.challenge_id
            JOIN user_play_match AS fupm ON fupm.match_id = m.match_id AND fupm.user_id = ?
            JOIN user_play_match AS supm ON supm.match_id = m.match_id AND supm.user_id != ?
            WHERE m.match_id = ?;
            """;

        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return new MatchStatusResponseMapper(
                        rs.getString("difficulty"),
                        rs.getString("programming_language"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("sample"),
                        rs.getString("player_name"),
                        rs.getInt("player_score"),
                        rs.getLong("player_id"),
                        rs.getString("second_name"),
                        rs.getInt("second_score"),
                        rs.getLong("second_id")
                );
            }
            return null;
        }, playerId, playerId, matchId);
    }

    public Long findRunningMatchOfUser(Long userId){
        String sql = "select m.match_id from `match` as m\n" +
                "join user_play_match as upm ON upm.match_id = m.match_id AND upm.user_id = ? AND m.status = 'RUNNING';";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql , userId);
        if (rs.next()){
            return rs.getLong("match_id");
        }
        return null;
    }

}