package com.kiranacademy.rpms.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "interview_rounds")
public class InterviewRound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interview_id")
    private Integer interviewId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber = 1;

    @Column(name = "round_type", nullable = false, length = 50)
    private String roundType;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "scheduled_time")
    private LocalTime scheduledTime;

    @Column(name = "mode", nullable = false, length = 20)
    private String mode = "ONLINE";

    @Column(name = "interviewer_name", length = 120)
    private String interviewerName;

    @Column(name = "meeting_or_location", length = 255)
    private String meetingOrLocation;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "SCHEDULED";

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToOne(mappedBy = "interviewRound", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private InterviewFeedback feedback;

    public InterviewRound() {}

    public Integer getInterviewId() { return interviewId; }
    public void setInterviewId(Integer interviewId) { this.interviewId = interviewId; }
    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }
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
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public InterviewFeedback getFeedback() { return feedback; }
    public void setFeedback(InterviewFeedback feedback) { this.feedback = feedback; }
}
