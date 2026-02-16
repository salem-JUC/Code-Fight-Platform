package com.code.duel.code.duel.DTO.ChallengeDTO;

import com.code.duel.code.duel.Model.Challenge;
import com.code.duel.code.duel.Model.TestCase;
import java.util.List;

public class ChallengeWithTestCasesDTO {
    private Challenge challenge;
    private List<TestCase> testCases;

    public ChallengeWithTestCasesDTO() {}

    public ChallengeWithTestCasesDTO(Challenge challenge, List<TestCase> testCases) {
        this.challenge = challenge;
        this.testCases = testCases;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }
}

