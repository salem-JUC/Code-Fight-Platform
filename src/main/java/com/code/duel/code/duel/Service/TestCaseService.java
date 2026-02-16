package com.code.duel.code.duel.Service;

import com.code.duel.code.duel.Model.TestCase;
import com.code.duel.code.duel.Repository.TestCaseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestCaseService {

    @Autowired
    TestCaseRepo testCaseRepo;

    public void createTestCase(TestCase testCase) {
        testCaseRepo.save(testCase);
    }

    public List<TestCase> getTestCasesByChallengeId(Long challengeId) {
        return testCaseRepo.findByChallengeId(challengeId);
    }

    public void updateTestCase(TestCase testCase) {
        testCaseRepo.update(testCase);
    }

    public void deleteTestCase(Long testCaseId) {
        testCaseRepo.deleteById(testCaseId);
    }
}

