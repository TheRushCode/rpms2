package com.kiranacademy.rpms.dto.interview;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class InterviewRequest {
    @NotNull(message = "Application ID is required")
    private Integer applicationId;

    @NotNull(message = "Round number is required")
    private Integer roundNumber = 1;

    @NotBlank(message = "Round type is required (TECHNICAL, HR, MANAGERIAL)")
    private String roundType;

    @NotNull(message = "Scheduled date is required")
    private LocalDate scheduledDate;

    private LocalTime scheduledTime;

    @NotBlank(message = "Mode is required (ONLINE, OFFLINE, TELEPHONIC)")
    private String mode = "ONLINE";

    private String interviewerName;
    private String meetingOrLocation;
    private String status = "SCHEDULED";

    public InterviewRequest() {}

    public Integer getApplicationId() { return applicationId; }
    public void setApplicationId(Integer applicationId) { this.applicationId = applicationId; }
    public Integer getRoundNumber() { return roundNumber; }
    public void setRoundNumber(Integer roundNumber) { this.roundNumber = roundNumber; }
    public String getRoundType() { return roundType; }
    public void setRoundType(String roundType) { this.roundType = roundType; }
    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }
    public LocalTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalTime scheduledTime) { this.scheduledTime = scheduledTime; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getInterviewerName() { return interviewerName; }
    public void setInterviewerName(String interviewerName) { this.interviewerName = interviewerName; }
    public String getMeetingOrLocation() { return meetingOrLocation; }
    public void setMeetingOrLocation(String meetingOrLocation) { this.meetingOrLocation = meetingOrLocation; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
