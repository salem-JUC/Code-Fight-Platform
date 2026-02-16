package com.code.duel.code.duel.Controller;

import com.code.duel.code.duel.DTO.ChallengeDTO.ChallengeWithTestCasesDTO;
import com.code.duel.code.duel.Model.Challenge;
import com.code.duel.code.duel.Model.TestCase;
import com.code.duel.code.duel.Service.ChallengeService;
import com.code.duel.code.duel.Service.TestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/challenges")
public class AdminChallengeController {

    @Autowired
    ChallengeService challengeService;

    @Autowired
    TestCaseService testCaseService;

    @GetMapping
    public ResponseEntity<List<Challenge>> getAllChallenges() {
        return ResponseEntity.ok(challengeService.getAllChallenges());
    }

    @PostMapping
    public ResponseEntity<Challenge> createChallenge(@RequestBody ChallengeWithTestCasesDTO dto) {
        Challenge challenge = dto.getChallenge();
        challengeService.createChallenge(challenge);
        
        if (dto.getTestCases() != null) {
            for (TestCase tc : dto.getTestCases()) {
                tc.setChallengeID(challenge.getChallengeID());
                testCaseService.createTestCase(tc);
            }
        }
        return ResponseEntity.ok(challenge);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChallengeWithTestCasesDTO> getChallenge(@PathVariable Long id) {
        Challenge challenge = challengeService.getChallengeById(id);
        if (challenge == null) {
            return ResponseEntity.notFound().build();
        }
        List<TestCase> testCases = testCaseService.getTestCasesByChallengeId(id);
        return ResponseEntity.ok(new ChallengeWithTestCasesDTO(challenge, testCases));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Challenge> updateChallenge(@PathVariable Long id, @RequestBody ChallengeWithTestCasesDTO dto) {
        Challenge existingChallenge = challengeService.getChallengeById(id);
        if (existingChallenge == null) {
            return ResponseEntity.notFound().build();
        }
        
        Challenge updatedChallenge = dto.getChallenge();
        updatedChallenge.setChallengeID(id);
        challengeService.updateChallenge(updatedChallenge);

        // For simplicity, we might delete old test cases and add new ones, or update existing ones.
        // Here we'll just update the challenge details. Test cases can be managed separately or extended here.
        // If test cases are provided, we could replace them.
        if (dto.getTestCases() != null) {
             List<TestCase> existingTestCases = testCaseService.getTestCasesByChallengeId(id);
             for(TestCase tc : existingTestCases) {
                 testCaseService.deleteTestCase(tc.getTestCaseID());
             }
             for (TestCase tc : dto.getTestCases()) {
                tc.setChallengeID(id);
                testCaseService.createTestCase(tc);
            }
        }

        return ResponseEntity.ok(updatedChallenge);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {
        challengeService.deleteChallenge(id);
        return ResponseEntity.ok().build();
    }
}

