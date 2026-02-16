package com.code.duel.code.duel.Model;

public class Submission {
    private Long submissionID;
    private Long challengeID;
    private Long submitterID;
    private String result;
    private String code;
    private String programmingLanguage;
    private String compileOutput;
    private String status;

    // Constructors
    public Submission() {}

    public Submission(Long submissionID, Long challengeID, Long submitterID, String result, String code, String programmingLanguage, String compileOutput, String status) {
        this.submissionID = submissionID;
        this.challengeID = challengeID;
        this.submitterID = submitterID;
        this.result = result;
        this.code = code;
        this.programmingLanguage = programmingLanguage;
        this.compileOutput = compileOutput;
        this.status = status;
    }
    public Submission(Long submissionID, Long challengeID, Long submitterID, String code, String programmingLanguage, String compileOutput, String status) {
        this.submissionID = submissionID;
        this.challengeID = challengeID;
        this.submitterID = submitterID;
        this.code = code;
        this.programmingLanguage = programmingLanguage;
        this.compileOutput = compileOutput;
        this.status = status;
    }

    // Getters and Setters
    public Long getSubmissionID() {
        return submissionID;
    }

    public void setSubmissionID(Long submissionID) {
        this.submissionID = submissionID;
    }

    public Long getChallengeID() {
        return challengeID;
    }

    public void setChallengeID(Long challengeID) {
        this.challengeID = challengeID;
    }

    public Long getSubmitterID() {
        return submitterID;
    }

    public void setSubmitterID(Long submitterID) {
        this.submitterID = submitterID;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "Submission{" +
                "submissionID=" + submissionID +
                ", challengeID=" + challengeID +
                ", submitterID=" + submitterID +
                ", result='" + result + '\'' +
                ", code='" + code + '\'' +
                ", programmingLanguage='" + programmingLanguage + '\'' +
                ", compileOutput='" + compileOutput + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
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
}
