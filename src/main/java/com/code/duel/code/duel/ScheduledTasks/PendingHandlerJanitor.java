package com.code.duel.code.duel.ScheduledTasks;

import com.code.duel.code.duel.Mappers.ResponseMapper.MatchResult;
import com.code.duel.code.duel.Model.UserPlayMatch;
import com.code.duel.code.duel.Service.MatchService;
import com.code.duel.code.duel.Service.PendingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@EnableScheduling
public class PendingHandlerJanitor {


    private final PendingService pendingService;
    private final MatchService matchService;
    private final SimpMessagingTemplate messagingTemplate;

    private static final Logger logger = LoggerFactory.getLogger(PendingHandlerJanitor.class);

    @Autowired
    public PendingHandlerJanitor(PendingService pendingService, MatchService matchService, SimpMessagingTemplate messagingTemplate) {
        this.pendingService = pendingService;
        this.matchService = matchService;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void handleExpiredUsers(){
        Set<Long> expiredUsers = pendingService.getExpiredUsers();

        for (Long userId: expiredUsers) {
            performQuitLogic(userId);
            pendingService.removeIfPending(userId);
            logger.info("User {} got kicked cause not connecting and removed from pending" , userId);
        }
    }

    private void performQuitLogic(Long userId) {
        Long matchId = matchService.getOnlyRunningMatchIdOfUser(userId);
        if (matchId == null) {
            logger.debug("No running match for expired user {}, skipping cleanup", userId);
            return;
        }

        try {
            UserPlayMatch winner = matchService.getTheOpponent(matchId , userId);
            matchService.endMatch(matchId , winner.getUserID());
            messagingTemplate.convertAndSend(
                    "/topic/match/" + matchId + "/ended",
                    new MatchResult(winner.getUserID(), winner.getUsername())
            );
        } catch (EmptyResultDataAccessException e) {
            logger.info("Expired user {} could not resolve opponent in match {}, skipping cleanup", userId, matchId);
        }
    }

}
