package com.code.duel.code.duel.Controller;

import com.code.duel.code.duel.Judge.Judge0Wrapper;
import com.code.duel.code.duel.Mappers.RequestMapper.SubmissionRequestMapper;
import com.code.duel.code.duel.Mappers.ResponseMapper.HitNotifcation;
import com.code.duel.code.duel.Mappers.ResponseMapper.MatchResult;
import com.code.duel.code.duel.Mappers.ResponseMapper.MatchStatusResponseMapper;
import com.code.duel.code.duel.Mappers.ResponseMapper.SubmissionResponse;
import com.code.duel.code.duel.Model.Challenge;
import com.code.duel.code.duel.Model.Submission;
import com.code.duel.code.duel.Model.User;
import com.code.duel.code.duel.Model.UserPlayMatch;
import com.code.duel.code.duel.Service.MatchService;
import com.code.duel.code.duel.Service.PendingService;
import com.code.duel.code.duel.Service.SubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Controller
public class MatchWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MatchService matchService;
    private final SubmissionService submissionService;
    private final PendingService pendingService;


    private static final Logger logger = LoggerFactory.getLogger(MatchWebSocketController.class);


    @Autowired
    public MatchWebSocketController(SimpMessagingTemplate messagingTemplate,
                                    MatchService matchService,
                                    SubmissionService submissionService,
                                    PendingService pendingService) {
        this.messagingTemplate = messagingTemplate;
        this.matchService = matchService;
        this.submissionService = submissionService;
        this.pendingService = pendingService;
    }

    @MessageMapping("/match/{matchId}/submit")
    public void handleCodeSubmission(@DestinationVariable Long matchId, @Payload SubmissionRequestMapper submission, Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        try {
            // 1. Validate the submission
            Submission pendingSubmission = submissionService.createSubmission(matchId, user.getUserID(), submission.getCode());
            System.out.println("new submissioj created with id " + pendingSubmission.getSubmissionID());
            submissionService.evaluateSubmission(pendingSubmission , matchId , principal.getName());
            System.out.println("evaluation submission is called (this is after evalauteSubmission calling");
        } catch (Exception e) {
            sendError(principal.getName(), "Submission error: " + e.getMessage());
        }
    }

    @MessageMapping("/match/{matchId}/quit")
    public void handlePlayerQuit(@DestinationVariable Long matchId, Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        logger.info("Player {} is quitting match {}", user.getUsername(), matchId);
        UserPlayMatch winner = matchService.getTheOpponent(matchId ,user.getUserID() );
        matchService.endMatch(matchId, winner.getUserID());
        pendingService.removeIfPending(user.getUserID());
        pendingService.removeIfPending(winner.getUserID());
        matchService.broadcastMatchEnd(matchId, winner.getUserID(), winner.getUsername());
    }




    private void sendError(String principalName, String errorMessage) {
        messagingTemplate.convertAndSendToUser(
                principalName,
                "/queue/errors",
                errorMessage
        );
    }

    @MessageMapping("/match/{matchId}/ready")
    public void handlePlayerReady(@DestinationVariable Long matchId, Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        MatchStatusResponseMapper status = matchService.getMatchStatus(matchId, user.getUserID());
        logger.info(principal.getName() + " player is ready - send to start with status " + status.toString());
        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/match/start",
                status
        );
    }


}
