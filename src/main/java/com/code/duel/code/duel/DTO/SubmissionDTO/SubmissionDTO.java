package com.code.duel.code.duel.DTO.SubmissionDTO;

// this DTO includes SubmissionID , challenge title , diffculty , programming language and result
public class SubmissionDTO {

    private long SubmissionID;
    private String ChallengeTitle;
    private String Difficulty;
    private String ProgrammingLanguage;
    private String Result;
    public SubmissionDTO(long submissionID, String challengeTitle, String difficulty, String programmingLanguage, String result) {
        SubmissionID = submissionID;
        ChallengeTitle = challengeTitle;
        Difficulty = difficulty;
        ProgrammingLanguage = programmingLanguage;
        Result = result;
    }

    public long getSubmissionID() {
        return SubmissionID;
    }

    public void setSubmissionID(long submissionID) {
        SubmissionID = submissionID;
    }

    public String getChallengeTitle() {
        return ChallengeTitle;
    }

    public void setChallengeTitle(String challengeTitle) {
        ChallengeTitle = challengeTitle;
    }

    public String getDifficulty() {
        return Difficulty;
    }

    public void setDifficulty(String difficulty) {
        Difficulty = difficulty;
    }

    public String getProgrammingLanguage() {
        return ProgrammingLanguage;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        ProgrammingLanguage = programmingLanguage;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    @Override
    public String toString() {
        return "SubmissionDTO{" +
                "SubmissionID=" + SubmissionID +
                ", ChallengeTitle='" + ChallengeTitle + '\'' +
                ", Difficulty='" + Difficulty + '\'' +
                ", ProgrammingLanguage='" + ProgrammingLanguage + '\'' +
                ", Result='" + Result + '\'' +
                '}';
    }
}
