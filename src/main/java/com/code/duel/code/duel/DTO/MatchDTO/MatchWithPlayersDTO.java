package com.code.duel.code.duel.DTO.MatchDTO;

public class MatchWithPlayersDTO {
    private Long matchID;
    private Long currentChallengeId;
    private String difficulty;
    private String programmingLanguage;
    private String status;
    private Long winnerId;
    private String players; // Comma separated list of players

    public MatchWithPlayersDTO() {}

    public MatchWithPlayersDTO(Long matchID, Long currentChallengeId, String difficulty, String programmingLanguage, String status, Long winnerId, String players) {
        this.matchID = matchID;
        this.currentChallengeId = currentChallengeId;
        this.difficulty = difficulty;
        this.programmingLanguage = programmingLanguage;
        this.status = status;
        this.winnerId = winnerId;
        this.players = players;
    }

    public Long getMatchID() {
        return matchID;
    }

    public void setMatchID(Long matchID) {
        this.matchID = matchID;
    }

    public Long getCurrentChallengeId() {
        return currentChallengeId;
    }

    public void setCurrentChallengeId(Long currentChallengeId) {
        this.currentChallengeId = currentChallengeId;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public String getPlayers() {
        return players;
    }

    public void setPlayers(String players) {
        this.players = players;
    }
}

