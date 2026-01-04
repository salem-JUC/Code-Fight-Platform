package com.code.duel.code.duel.Repository;

import com.code.duel.code.duel.DTO.SubmissionDTO.SubmissionDTO;
import com.code.duel.code.duel.DTO.SubmissionDTO.SubmissionDetailsDTO;
import com.code.duel.code.duel.DTO.SubmissionDTO.SubmissionWithUserDTO;
import com.code.duel.code.duel.Model.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SubmissionRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Save a new submission
    public void save(Submission submission) {
        String sql = "INSERT INTO Submission (submissionID, ChallengeID, submitterID, Result, Code, ProgrammingLanguage) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, submission.getSubmissionID(), submission.getChallengeID(), submission.getSubmitterID(), submission.getResult(), submission.getCode(), submission.getProgrammingLanguage());

    }

    // Find a submission by ID
    public Submission findById(Long submissionID) {
        String sql = "SELECT * FROM Submission WHERE submissionID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{submissionID}, (rs, rowNum) ->
                new Submission(
                        rs.getLong("submissionID"),
                        rs.getLong("ChallengeID"),
                        rs.getLong("submitterID"),
                        rs.getString("Result"),
                        rs.getString("Code"),
                        rs.getString("ProgrammingLanguage")
                ));
    }

    // Find all submissions for a challenge
    public List<Submission> findByChallengeId(Long challengeID) {
        String sql = "SELECT * FROM Submission WHERE ChallengeID = ?";
        return jdbcTemplate.query(sql, new Object[]{challengeID}, (rs, rowNum) ->
                new Submission(
                        rs.getLong("submissionID"),
                        rs.getLong("ChallengeID"),
                        rs.getLong("submitterID"),
                        rs.getString("Result"),
                        rs.getString("Code"),
                        rs.getString("ProgrammingLanguage")
                ));
    }

    public List<Submission> findBysubmitterId(Long submitterId) {
        String sql = "SELECT * FROM Submission WHERE SUBMITTERID  = ?";
        return jdbcTemplate.query(sql, new Object[]{submitterId}, (rs, rowNum) ->
                new Submission(
                        rs.getLong("submissionID"),
                        rs.getLong("ChallengeID"),
                        rs.getLong("submitterID"),
                        rs.getString("Result"),
                        rs.getString("Code"),
                        rs.getString("ProgrammingLanguage")
                ));
    }

    public List<SubmissionDTO> getSubmissionsOfUser(Long userId){
        String sql = "select s.SUBMISSIONID , c.TITLE  , c.DIFFICULTY , s.PROGRAMMINGLANGUAGE , s.RESULT \n" +
                "from SUBMISSION  s\n" +
                "inner join CHALLENGE c on s.CHALLENGEID = c.CHALLENGEID\n" +
                "where s.SUBMITTERID = ? ;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql , userId);
        List<SubmissionDTO> submissions = new ArrayList<>();
        while (rowSet.next()){
            SubmissionDTO submission = new SubmissionDTO(
                    rowSet.getLong("SUBMISSIONID"),
                    rowSet.getString("TITLE"),
                    rowSet.getString("DIFFICULTY"),
                    rowSet.getString("PROGRAMMINGLANGUAGE"),
                    rowSet.getString("RESULT")
            );
            submissions.add(submission);
        }
        return submissions;
    }

    // Update a submission
    public void update(Submission submission) {
        String sql = "UPDATE Submission SET ChallengeID = ?, submitterID = ?, Result = ?, Code = ?, ProgrammingLanguage = ? WHERE submissionID = ?";
        jdbcTemplate.update(sql, submission.getChallengeID(), submission.getSubmitterID(), submission.getResult(), submission.getCode(), submission.getProgrammingLanguage(), submission.getSubmissionID());
    }

    // Delete a submission by ID
    public void deleteById(Long submissionID) {
        String sql = "DELETE FROM Submission WHERE submissionID = ?";
        jdbcTemplate.update(sql, submissionID);
    }

    public SubmissionDetailsDTO getSubmissionDetails(Long submissionId) {
        String sql = """
                select u.USERNAME , s.CODE , s.RESULT , s.PROGRAMMINGLANGUAGE , c.TITLE , c.DESCRIPTION , c.DIFFICULTY , c.ChallengeID
                from SUBMISSION s
                inner join `user` u on s.SUBMITTERID = u.USERID
                inner join CHALLENGE c on s.CHALLENGEID = c.CHALLENGEID
                where s.SUBMISSIONID = ? ;
                """;

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql , submissionId);
        SubmissionDetailsDTO submissionDetailsDTO = null;
        if (rowSet.next()){
            submissionDetailsDTO =
                    new SubmissionDetailsDTO(
                            rowSet.getString("USERNAME"),
                            rowSet.getString("CODE"),
                            rowSet.getString("RESULT"),
                            rowSet.getString("PROGRAMMINGLANGUAGE"),
                            rowSet.getString("TITLE"),
                            rowSet.getString("DESCRIPTION"),
                            rowSet.getString("DIFFICULTY"),
                            rowSet.getLong("CHALLENGEID")
                    );
        }
        return submissionDetailsDTO;
    }

    public List<SubmissionWithUserDTO> getAllSubmissionsWithUsernames(Long challengeId) {
        String sql = """
                select u.USERNAME , s.SUBMISSIONID , c.TITLE , c.DIFFICULTY , s.PROGRAMMINGLANGUAGE , s.RESULT\s
                from SUBMISSION s
                join `user` u on u.USERID = s.SUBMITTERID\s
                join CHALLENGE c on c.CHALLENGEID = s.CHALLENGEID\s
                where c.CHALLENGEID = ? ;
                """;

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, challengeId);
        List<SubmissionWithUserDTO> submissions = new ArrayList<>();
        while (rowSet.next()){
            SubmissionWithUserDTO submission =
                    new SubmissionWithUserDTO(
                            rowSet.getString("USERNAME"),
                            rowSet.getLong("SUBMISSIONID"),
                            rowSet.getString("TITLE"),
                            rowSet.getString("DIFFICULTY"),
                            rowSet.getString("PROGRAMMINGLANGUAGE"),
                            rowSet.getString("RESULT")
                    );
            submissions.add(submission);
        }
        return submissions;
    }
}
