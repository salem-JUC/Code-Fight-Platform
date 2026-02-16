package com.code.duel.code.duel.Mappers.ResponseMapper;

public class SubmissionResponse {
    private boolean accepted;
    private String message;
    private String compileOutput;

    
    public SubmissionResponse(boolean accepted, String message, String compileOutput) {
        this.accepted = accepted;
        this.message = message;
        this.compileOutput = compileOutput;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getCompileOutput() {
        return compileOutput;
    }

    public void setCompileOutput(String compileOutput) {
        this.compileOutput = compileOutput;
    }
}
