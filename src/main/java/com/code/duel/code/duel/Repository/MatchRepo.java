package com.code.duel.code.duel.Repository;


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

import javax.sql.RowSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;


@Repository
public class MatchRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Save a new match
    public void save(Match match) {
        String sql = "INSERT INTO `match` (matchID, current_challenge_id, difficulty, programmingLanguage,status, winnerId) VALUES (?,?,? ,?, ? ,?)";

        jdbcTemplate.update(sql,
                match.getMatchID(),
                match.getCurrentChallengeId(),
                match.getDifficulty(),
                match.getProgrammingLanguage(),
                match.getStatus(),
                match.getWinnerId());
    }

    // Find a match by ID
    public Match findById(Long matchID) {
        String sql = "SELECT * FROM `match` WHERE matchID = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{matchID}, (rs, rowNum) ->
                new Match(
                        rs.getLong("matchID"),
                        rs.getLong("current_challenge_id"),
                        rs.getString("difficulty"),
                        rs.getString("programmingLanguage"),
                        rs.getString("status"),
                        rs.getLong("winnerId")
                ));

    }

    // Find all matches
    public List<Match> findAll() {
        String sql = "SELECT * FROM `match`";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Match(
                        rs.getLong("matchID"),
                        rs.getLong("current_challenge_id"),
                        rs.getString("difficulty"),
                        rs.getString("programmingLanguage"),
                        rs.getString("status"),
                        rs.getLong("matchID")
                ));
    }

    // Update a match (including challenge ID)
    public void update(Match match) {
        String sql = "UPDATE `match` SET status = ?, current_challenge_id = ?, winnerId = ? WHERE matchID = ?";
        jdbcTemplate.update(sql,
                match.getStatus(),
                match.getCurrentChallengeId(),
                match.getWinnerId(),
                match.getMatchID());
    }


    // Update only the current challenge ID
    public void updateChallenge(Long matchId, Long challengeId) {
        String sql = "UPDATE `match` SET current_challenge_id = ? WHERE matchID = ?";
        jdbcTemplate.update(sql, challengeId, matchId);
    }

    // Delete a match by ID
    public void deleteById(Long matchID) {
        String sql = "DELETE FROM `match` WHERE matchID = ?";
        jdbcTemplate.update(sql, matchID);
    }

    // Find matches by status
    public List<Match> findByStatus(String status) {
        String sql = "SELECT * FROM `match` WHERE status = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Match(
                        rs.getLong("matchID"),
                        rs.getLong("current_challenge_id"),
                        rs.getString("difficulty"),
                        rs.getString("programmingLanguage"),
                        rs.getString("status"),
                        rs.getLong("winnerId")
                ));
    }

    // Find first pending match
    public Optional<Match> findFirstPending() {
        String sql = "SELECT * FROM `match` WHERE status = 'PENDING' ORDER BY matchID ASC LIMIT 1";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Match(
                        rs.getLong("matchID"),
                        rs.getLong("current_challenge_id"),
                        rs.getString("difficulty"),
                        rs.getString("programmingLanguage"),
                        rs.getString("status"),
                        rs.getLong("winnerId")
                )).stream().findFirst();
    }

    public List<Match> findAllMatchesByUserIdOrderByRecent(Long userId) {
        String sql = """
            SELECT m.matchID, m.current_challenge_id, m.difficulty, 
                   m.programmingLanguage, m.status, m.winnerId
            FROM `match` m
            JOIN user_play_match upm ON m.matchID = upm.matchID
            WHERE upm.userID = ?
            ORDER BY m.matchID DESC
            """;

        return jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) -> {
            Match match = new Match();
            match.setMatchID(rs.getLong("matchID"));
            match.setCurrentChallengeId(rs.getLong("current_challenge_id"));
            match.setDifficulty(rs.getString("difficulty"));
            match.setProgrammingLanguage(rs.getString("programmingLanguage"));
            match.setStatus(rs.getString("status"));
            match.setWinnerId(rs.getLong("winnerId"));
            return match;
        });
    }

    public List<Match> findAllMatchesWinnedByUserIdOrderByRecent(Long userId) {
        String sql = """
            SELECT m.matchID, m.current_challenge_id, m.difficulty, 
                   m.programmingLanguage, m.status, m.winnerId
            FROM `match` m
            WHERE m.winnerId = ?
            ORDER BY m.matchID DESC
            """;

        return jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) -> {
            Match match = new Match();
            match.setMatchID(rs.getLong("matchID"));
            match.setCurrentChallengeId(rs.getLong("current_challenge_id"));
            match.setDifficulty(rs.getString("difficulty"));
            match.setProgrammingLanguage(rs.getString("programmingLanguage"));
            match.setStatus(rs.getString("status"));
            match.setWinnerId(rs.getLong("winnerId"));
            return match;
        });
    }

    public MatchStatusResponseMapper queryMatchStatus(Long matchId , Long playerId){
        String sql = """
                select m.DIFFICULTY ,
                m.PROGRAMMINGLANGUAGE ,
                c.TITLE ,
                c.DESCRIPTION ,
                c.DIFFICULTY ,
                c.SAMPLE ,
                fupm.USERNAME as PlayerName ,
                fupm.USERSCORE as PlayerScore ,
                fupm.USERID as PlayerId ,
                supm.USERNAME as SecondName ,
                supm.USERSCORE  as SecondScore ,
                supm.USERID as SecondId
                from `match` as m
                join CHALLENGE as c ON c.CHALLENGEID = m.CURRENT_CHALLENGE_ID
                join USER_PLAY_MATCH  as fupm ON fupm.MATCHID = m.MATCHID AND fupm.USERID = ?
                join USER_PLAY_MATCH  as supm ON supm.MATCHID = m.MATCHID AND supm.USERID != ?
                where m.MATCHID = ?;
                """;

        return jdbcTemplate.query(sql, rs -> {
            // .next() moves the cursor to the first row and returns false if empty
            if (rs.next()) {
                return new MatchStatusResponseMapper(
                        rs.getString("DIFFICULTY"),
                        rs.getString("PROGRAMMINGLANGUAGE"),
                        rs.getString("TITLE"),
                        rs.getString("DESCRIPTION"),
                        rs.getString("SAMPLE"),
                        rs.getString("PlayerName"),
                        rs.getInt("PlayerScore"),
                        rs.getLong("PlayerId"),
                        rs.getString("SecondName"),
                        rs.getInt("SecondScore"),
                        rs.getLong("SecondId")
                );
            }
            else
                return null; // Return null if match not found
        }, playerId, playerId, matchId);
    }

    public Long findRunningMatchOfUser(Long userId){
        String sql = "select m.MATCHID from `match` as m\n" +
                "join USER_PLAY_MATCH as upm ON upm.MATCHID = m.MATCHID AND upm.USERID = ? AND m.STATUS = 'RUNNING';";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql , userId);
        if (rs.next()){
            return rs.getLong("MATCHID");
        }
        return null;
    }

}