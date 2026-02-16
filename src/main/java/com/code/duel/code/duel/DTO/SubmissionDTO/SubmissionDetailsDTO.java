package com.code.duel.code.duel.DTO.SubmissionDTO;

public class SubmissionDetailsDTO {
        // includes .USERNAME , s.CODE , s.RESULT , s.PROGRAMMINGLANGUAGE , c.TITLE , c.DESCRIPTION , c.DIFFICULTY
        private String username;
        private String code;
        private String result;
        private String programmingLanguage;
        private String title;
        private String description;
        private String difficulty;
        private Long challengeId;
        private String compileOutput;
        private String status;
        public SubmissionDetailsDTO(String username, String code, String result, String programmingLanguage, String title, String description, String difficulty, Long challengeId, String compileOutput, String status) {
            this.username = username;
            this.code = code;
            this.result = result;
            this.programmingLanguage = programmingLanguage;
            this.title = title;
            this.description = description;
            this.difficulty = difficulty;
            this.challengeId = challengeId;
            this.compileOutput = compileOutput;
            this.status = status;
        }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Long getChallengeId() {
        return challengeId;
    }
    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public String getCompileOutput() {
        return compileOutput;
    }

    public void setCompileOutput(String compileOutput) {
        this.compileOutput = compileOutput;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // toString
        @Override
        public String toString() {
            return "SubmissionDetailsDTO{" +
                    "username='" + username + '\'' +
                    ", code='" + code + '\'' +
                    ", result='" + result + '\'' +
                    ", programmingLanguage='" + programmingLanguage + '\'' +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", difficulty='" + difficulty + '\'' +
                    ", challengeId=" + challengeId +
                    ", compileOutput='" + compileOutput + '\'' +
                    ", status='" + status + '\'' +
                    '}';
        }
}
