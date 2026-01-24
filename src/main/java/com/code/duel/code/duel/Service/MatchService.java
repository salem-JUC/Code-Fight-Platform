package com.code.duel.code.duel.Service;

import com.code.duel.code.duel.Exception.MatchNotFoundException;
import com.code.duel.code.duel.Judge.Judge0Wrapper;
import com.code.duel.code.duel.Mappers.ResponseMapper.MatchStatusResponseMapper;
import com.code.duel.code.duel.Model.Challenge;
import com.code.duel.code.duel.Model.Match;
import com.code.duel.code.duel.Model.UserPlayMatch;
import com.code.duel.code.duel.Repository.ChallengeRepo;
import com.code.duel.code.duel.Repository.MatchRepo;
import com.code.duel.code.duel.Repository.UserPlayMatchRepo;
import com.code.duel.code.duel.Repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

    @Autowired
    MatchRepo matchRepo;
    @Autowired
    UserPlayMatchRepo userPlayMatchRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    ChallengeRepo challengeRepo;

    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

    public Match createMatch(Long playerId , String difficulty , String programmingLanguage){
        logger.info("Creating match with player ID: {}, difficulty: {}, programming language: {}", playerId, difficulty, programmingLanguage);
        Match newMatch = new Match();
        newMatch.setStatus("PENDING");
        newMatch.setDifficulty(difficulty);
        newMatch.setProgrammingLanguage(programmingLanguage);
        newMatch.setWinnerId(0L);
        newMatch = matchRepo.save(newMatch);

        UserPlayMatch userPlayMatch1 = new UserPlayMatch();
        userPlayMatch1.setUserID(playerId);
        userPlayMatch1.setMatchID(newMatch.getMatchID());
        String username = userRepo.findById(playerId).getUsername();
        userPlayMatch1.setUsername(username);
        userPlayMatch1.setUserScore(3); // init score
        userPlayMatchRepo.save(userPlayMatch1);

        return newMatch;
    }

    public Match joinMatch(Long matchId , Long playerId){
        logger.info("Joining match with ID: {}, player ID: {}", matchId, playerId);
        Match wantedMatch;
        try {
            wantedMatch = matchRepo.findById(matchId);
        }catch (EmptyResultDataAccessException e){
            throw new MatchNotFoundException(matchId);
        }
        if (!wantedMatch.getStatus().equals("PENDING"))
            throw new MatchNotFoundException(matchId);
        UserPlayMatch userPlayMatch = new UserPlayMatch(playerId, wantedMatch.getMatchID(),userRepo.findById(playerId).getUsername() , 3);
        userPlayMatchRepo.save(userPlayMatch);
        wantedMatch.setStatus("RUNNING");
        wantedMatch.setCurrentChallengeId(1L);
        matchRepo.update(wantedMatch);
        assignChallenge(wantedMatch.getMatchID());
        return wantedMatch;
    }

    public Challenge assignChallenge(Long matchId){

        Match match = matchRepo.findById(matchId);
        String difficulty = match.getDifficulty();
        Challenge randomChallenge = challengeRepo.findRandomWithDifficulty(difficulty);
        match.setCurrentChallengeId(randomChallenge.getChallengeID());
        logger.info("Assigning challenge with ID: {}, title : {} to match with ID: {} diffculty: {}", randomChallenge.getChallengeID(),randomChallenge.getTitle(), matchId, randomChallenge.getDifficulty());
        matchRepo.update(match);
        return randomChallenge;
    }

    public void handleCorrectSubmmission(Long matchId, Long playerId, Long challengeId){
        logger.info("Handling correct submission for match ID: {}, player ID: {}, challenge ID: {}", matchId, playerId, challengeId);
        Match match = matchRepo.findById(matchId);
        if (match.getCurrentChallengeId() != challengeId)
            throw new MatchNotFoundException(matchId);

        // Decrease the opponent's score
        UserPlayMatch opponent = userPlayMatchRepo.findTheOpponent(playerId, matchId);
        opponent.setUserScore(opponent.getUserScore() - 1);
        userPlayMatchRepo.update(opponent);
        if (opponent.getUserScore() <= 0) {
            endMatch(matchId , playerId);
        }else {
            assignChallenge(matchId);
        }
    }

    public void endMatch(Long matchId, Long winnerId) {
        logger.info("Ending match with ID: {}, winner ID: {}", matchId, winnerId);
        Match match = matchRepo.findById(matchId);
        match.setStatus("FINISHED");
        match.setWinnerId(winnerId);
        matchRepo.update(match);
    }

    public MatchStatusResponseMapper getMatchStatus(Long matchId , Long playerId){
        MatchStatusResponseMapper msrm = matchRepo.queryMatchStatus(matchId , playerId);

        return msrm;
    }



    public boolean isMatchReady(Long matchId) {
        Match match = matchRepo.findById(matchId);
        return "RUNNING".equals(match.getStatus());
    }

    public boolean isMatchFinished(Long matchId) {
        Match match = matchRepo.findById(matchId);
        return "FINISHED".equals(match.getStatus());
    }

    public List<Match> getAllMatches() {
        return matchRepo.findAll();
    }
    public List<Match> getMatchesByUserId(Long userId) {
        List<Match> matches;
        try {
            matches = matchRepo.findAllMatchesByUserIdOrderByRecent(userId);
        }catch (EmptyResultDataAccessException e){
            throw new MatchNotFoundException(userId);
        }
        return matches;
    }

    public List<Match> getMatchesWinnerId(Long winnerId) {
        List<Match> matches;
        try {
            matches = matchRepo.findAllMatchesWinnedByUserIdOrderByRecent(winnerId);
        }catch (EmptyResultDataAccessException e){
            throw new MatchNotFoundException(winnerId);
        }
        return matches;
    }

    public String getWinLoseRationByUserId(Long userId){
        try {
            int winnedMatches = getMatchesWinnerId(userId).size();
            int allMatches = getMatchesByUserId(userId).size();
            return "" + winnedMatches + "/" + (allMatches-winnedMatches) + "";
        }catch (MatchNotFoundException e){
            return "0/0";
        }catch (Exception e){
            return "0/0";
        }

    }

    public UserPlayMatch getTheOpponent(Long matchId , Long playerId){
        return userPlayMatchRepo.findTheOpponent(playerId , matchId);
    }

    public Long getOnlyRunningMatchIdOfUser(Long userId){
        return matchRepo.findRunningMatchOfUser(userId);
    }

}
