package com.code.duel.code.duel.Service;

import com.code.duel.code.duel.DTO.SubmissionDTO.SubmissionDTO;
import com.code.duel.code.duel.DTO.SubmissionDTO.SubmissionDetailsDTO;
import com.code.duel.code.duel.DTO.SubmissionDTO.SubmissionWithUserDTO;
import com.code.duel.code.duel.Judge.EvaluationModule;
import com.code.duel.code.duel.Model.Match;
import com.code.duel.code.duel.Model.Submission;
import com.code.duel.code.duel.Model.TestCase;
import com.code.duel.code.duel.Repository.ChallengeRepo;
import com.code.duel.code.duel.Repository.MatchRepo;
import com.code.duel.code.duel.Repository.SubmissionRepo;
import com.code.duel.code.duel.Repository.TestCaseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmissionService {
    @Autowired
    SubmissionRepo submissionRepo;
    @Autowired
    EvaluationModule evaluationModule;
    @Autowired
    MatchRepo matchRepo;
    @Autowired
    ChallengeRepo challengeRepo;
    @Autowired
    TestCaseRepo testCaseRepo;

    public List<Submission> getAllSubmissionsOfChallenge(Long challengeId) {
        return submissionRepo.findByChallengeId(challengeId);
    }

    public List<SubmissionDTO> getAllSubmissionsOfSubmitter(Long submitterId) {
        return submissionRepo.getSubmissionsOfUser(submitterId);
    }

    public Submission getSubmissionById(Long submissionId) {
        return submissionRepo.findById(submissionId);
    }

    public Submission createSubmission(Long matchId ,  Long submitterId , String code) {
        Match match = matchRepo.findById(matchId);
        Submission submission = new Submission();
        submission.setCode(code);
        submission.setChallengeID(match.getCurrentChallengeId());
        submission.setProgrammingLanguage(match.getProgrammingLanguage());
        submission.setSubmitterID(submitterId);
        List<TestCase> testCases = testCaseRepo.findByChallengeId(match.getCurrentChallengeId());
        
        // Updated to use the new evaluate signature
        submission = evaluationModule.evaluate(submission , testCases);
        
        System.out.println("Submission evaluated: assigned" + submission.getResult());
        submission = submissionRepo.save(submission);
        System.out.println("Submission created: " + submission.getSubmissionID());
        return submission;
    }

    public SubmissionDetailsDTO getSubmissionDetailsDTO(Long submissionId) {
        return submissionRepo.getSubmissionDetails(submissionId);
    }

    public List<SubmissionWithUserDTO> getAllSubmissionsWithUsernames(Long challengeId) {
        return submissionRepo.getAllSubmissionsWithUsernames(challengeId);
    }
}
