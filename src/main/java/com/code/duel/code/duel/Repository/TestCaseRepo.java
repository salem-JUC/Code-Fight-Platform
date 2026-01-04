package com.code.duel.code.duel.Repository;


import com.code.duel.code.duel.Model.TestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TestCaseRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Save a new test case
    public void save(TestCase testCase) {
        String sql = "INSERT INTO TestCase (testCaseID, ChallengeID, `input`, ExpectedOutput) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, testCase.getTestCaseID(), testCase.getChallengeID(), testCase.getInput(), testCase.getExpectedOutput());
    }

    // Find a test case by ID
    public TestCase findById(Long testCaseID) {
        String sql = "SELECT * FROM TestCase WHERE testCaseID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{testCaseID}, (rs, rowNum) ->
                new TestCase(
                        rs.getLong("testCaseID"),
                        rs.getLong("ChallengeID"),
                        rs.getString("input"),
                        rs.getString("ExpectedOutput")
                ));
    }

    // Find all test cases for a challenge
    public List<TestCase> findByChallengeId(Long challengeID) {
        String sql = "SELECT * FROM TestCase WHERE ChallengeID = ?";
        return jdbcTemplate.query(sql, new Object[]{challengeID}, (rs, rowNum) ->
                new TestCase(
                        rs.getLong("testCaseID"),
                        rs.getLong("ChallengeID"),
                        rs.getString("input"),
                        rs.getString("ExpectedOutput")
                ));
    }

    // Update a test case
    public void update(TestCase testCase) {
        String sql = "UPDATE TestCase SET ChallengeID = ?, `input` = ?, ExpectedOutput = ? WHERE testCaseID = ?";
        jdbcTemplate.update(sql, testCase.getChallengeID(), testCase.getInput(), testCase.getExpectedOutput(), testCase.getTestCaseID());
    }

    // Delete a test case by ID
    public void deleteById(Long testCaseID) {
        String sql = "DELETE FROM TestCase WHERE testCaseID = ?";
        jdbcTemplate.update(sql, testCaseID);
    }
}
