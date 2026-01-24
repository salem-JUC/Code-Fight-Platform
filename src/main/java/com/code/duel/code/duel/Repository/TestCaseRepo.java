package com.code.duel.code.duel.Repository;


import com.code.duel.code.duel.Model.TestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class TestCaseRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Save a new test case
    public void save(TestCase testCase) {
        String sql = "INSERT INTO test_case (challenge_id, `input`, expected_output) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, testCase.getChallengeID());
            ps.setString(2, testCase.getInput());
            ps.setString(3, testCase.getExpectedOutput());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            testCase.setTestCaseID(key.longValue());
        }
    }

    // Find a test case by ID
    public TestCase findById(Long testCaseID) {
        String sql = "SELECT * FROM test_case WHERE test_case_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{testCaseID}, (rs, rowNum) ->
                new TestCase(
                        rs.getLong("test_case_id"),
                        rs.getLong("challenge_id"),
                        rs.getString("input"),
                        rs.getString("expected_output")
                ));
    }

    // Find all test cases for a challenge
    public List<TestCase> findByChallengeId(Long challengeID) {
        String sql = "SELECT * FROM test_case WHERE challenge_id = ?";
        return jdbcTemplate.query(sql, new Object[]{challengeID}, (rs, rowNum) ->
                new TestCase(
                        rs.getLong("test_case_id"),
                        rs.getLong("challenge_id"),
                        rs.getString("input"),
                        rs.getString("expected_output")
                ));
    }

    // Update a test case
    public void update(TestCase testCase) {
        String sql = "UPDATE test_case SET challenge_id = ?, `input` = ?, expected_output = ? WHERE test_case_id = ?";
        jdbcTemplate.update(sql, testCase.getChallengeID(), testCase.getInput(), testCase.getExpectedOutput(), testCase.getTestCaseID());
    }

    // Delete a test case by ID
    public void deleteById(Long testCaseID) {
        String sql = "DELETE FROM test_case WHERE test_case_id = ?";
        jdbcTemplate.update(sql, testCaseID);
    }
}
