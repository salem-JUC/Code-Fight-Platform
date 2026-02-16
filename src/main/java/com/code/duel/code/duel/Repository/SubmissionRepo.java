package com.code.duel.code.duel.Repository;

import com.code.duel.code.duel.DTO.SubmissionDTO.SubmissionDTO;
import com.code.duel.code.duel.DTO.SubmissionDTO.SubmissionDetailsDTO;
import com.code.duel.code.duel.DTO.SubmissionDTO.SubmissionWithUserDTO;
import com.code.duel.code.duel.Model.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SubmissionRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Save a new submission
    public Submission save(Submission submission) {
        String sql = "INSERT INTO submission (challenge_id, submitter_id, result, code, programming_language, compile_output, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, submission.getChallengeID());
            ps.setLong(2, submission.getSubmitterID());
            ps.setString(3, submission.getResult());
            ps.setString(4, submission.getCode());
            ps.setString(5, submission.getProgrammingLanguage());
            ps.setString(6, submission.getCompileOutput());
            ps.setString(7, submission.getStatus());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            submission.setSubmissionID(key.longValue());
        }
        return submission;
    }

    // Find a submission by ID
    public Submission findById(Long submissionID) {
        String sql = "SELECT * FROM submission WHERE submission_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{submissionID}, (rs, rowNum) ->
                new Submission(
                        rs.getLong("submission_id"),
                        rs.getLong("challenge_id"),
                        rs.getLong("submitter_id"),
                        rs.getString("result"),
                        rs.getString("code"),
                        rs.getString("programming_language"),
                        rs.getString("compile_output"),
                        rs.getString("status")
                ));
    }

    // Find all submissions for a challenge
    public List<Submission> findByChallengeId(Long challengeID) {
        String sql = "SELECT * FROM submission WHERE challenge_id = ?";
        return jdbcTemplate.query(sql, new Object[]{challengeID}, (rs, rowNum) ->
                new Submission(
                        rs.getLong("submission_id"),
                        rs.getLong("challenge_id"),
                        rs.getLong("submitter_id"),
                        rs.getString("result"),
                        rs.getString("code"),
                        rs.getString("programming_language"),
                        rs.getString("compile_output"),
                        rs.getString("status")
                ));
    }

    public List<Submission> findBysubmitterId(Long submitterId) {
        String sql = "SELECT * FROM submission WHERE submitter_id = ?";
        return jdbcTemplate.query(sql, new Object[]{submitterId}, (rs, rowNum) ->
                new Submission(
                        rs.getLong("submission_id"),
                        rs.getLong("challenge_id"),
                        rs.getLong("submitter_id"),
                        rs.getString("result"),
                        rs.getString("code"),
                        rs.getString("programming_language"),
                        rs.getString("compile_output"),
                        rs.getString("status")
                ));
    }

    public List<SubmissionDTO> getSubmissionsOfUser(Long userId){
        String sql = """
                select s.submission_id, c.title, c.difficulty, s.programming_language, s.result
                from submission s
                inner join challenge c on s.challenge_id = c.challenge_id
                where s.submitter_id = ?;
                """;
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql , userId);
        List<SubmissionDTO> submissions = new ArrayList<>();
        while (rowSet.next()){
            SubmissionDTO submission = new SubmissionDTO(
                    rowSet.getLong("submission_id"),
                    rowSet.getString("title"),
                    rowSet.getString("difficulty"),
                    rowSet.getString("programming_language"),
                    rowSet.getString("result")
            );
            submissions.add(submission);
        }
        return submissions;
    }

    // Update a submission
    public void update(Submission submission) {
        String sql = "UPDATE submission SET challenge_id = ?, submitter_id = ?, result = ?, code = ?, programming_language = ?, compile_output = ?, status = ? WHERE submission_id = ?";
        jdbcTemplate.update(sql, submission.getChallengeID(), submission.getSubmitterID(), submission.getResult(), submission.getCode(), submission.getProgrammingLanguage(), submission.getCompileOutput(), submission.getStatus(), submission.getSubmissionID());
    }

    // Delete a submission by ID
    public void deleteById(Long submissionID) {
        String sql = "DELETE FROM submission WHERE submission_id = ?";
        jdbcTemplate.update(sql, submissionID);
    }

    public SubmissionDetailsDTO getSubmissionDetails(Long submissionId) {
        String sql = """
                select u.username, s.code, s.result, s.programming_language, c.title, c.description, c.difficulty, c.challenge_id, s.compile_output, s.status
                from submission s
                inner join `user` u on s.submitter_id = u.user_id
                inner join challenge c on s.challenge_id = c.challenge_id
                where s.submission_id = ?;
                """;

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql , submissionId);
        SubmissionDetailsDTO submissionDetailsDTO = null;
        if (rowSet.next()){
            submissionDetailsDTO =
                    new SubmissionDetailsDTO(
                            rowSet.getString("username"),
                            rowSet.getString("code"),
                            rowSet.getString("result"),
                            rowSet.getString("programming_language"),
                            rowSet.getString("title"),
                            rowSet.getString("description"),
                            rowSet.getString("difficulty"),
                            rowSet.getLong("challenge_id"),
                            rowSet.getString("compile_output"),
                            rowSet.getString("status")
                    );
        }
        return submissionDetailsDTO;
    }

    public List<SubmissionWithUserDTO> getAllSubmissionsWithUsernames(Long challengeId) {
        String sql = """
                select u.username, s.submission_id, c.title, c.difficulty, s.programming_language, s.result
                from submission s
                join `user` u on u.user_id = s.submitter_id
                join challenge c on c.challenge_id = s.challenge_id
                where c.challenge_id = ?;
                """;

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, challengeId);
        List<SubmissionWithUserDTO> submissions = new ArrayList<>();
        while (rowSet.next()){
            SubmissionWithUserDTO submission =
                    new SubmissionWithUserDTO(
                            rowSet.getString("username"),
                            rowSet.getLong("submission_id"),
                            rowSet.getString("title"),
                            rowSet.getString("difficulty"),
                            rowSet.getString("programming_language"),
                            rowSet.getString("result")
                    );
            submissions.add(submission);
        }
        return submissions;
    }

    public List<SubmissionDTO> findAll() {
        String sql = """
                select s.submission_id, c.title, c.difficulty, s.programming_language, s.result
                from submission s
                inner join challenge c on s.challenge_id = c.challenge_id
                """;
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        List<SubmissionDTO> submissions = new ArrayList<>();
        while (rowSet.next()){
            SubmissionDTO submission = new SubmissionDTO(
                    rowSet.getLong("submission_id"),
                    rowSet.getString("title"),
                    rowSet.getString("difficulty"),
                    rowSet.getString("programming_language"),
                    rowSet.getString("result")
            );
            submissions.add(submission);
        }
        return submissions;
    }
}
