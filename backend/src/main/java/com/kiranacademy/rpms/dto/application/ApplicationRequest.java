package com.kiranacademy.rpms.dto.application;

import jakarta.validation.constraints.NotNull;

public class ApplicationRequest {
    @NotNull(message = "Candidate ID is required")
    private Integer candidateId;

    @NotNull(message = "Job ID is required")
    private Integer jobId;

    private String source = "PORTAL";

    public ApplicationRequest() {}

    public ApplicationRequest(Integer candidateId, Integer jobId) {
        this.candidateId = candidateId;
        this.jobId = jobId;
        this.source = "PORTAL";
    }

    public Integer getCandidateId() { return candidateId; }
    public void setCandidateId(Integer candidateId) { this.candidateId = candidateId; }
    public Integer getJobId() { return jobId; }
    public void setJobId(Integer jobId) { this.jobId = jobId; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
