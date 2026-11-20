package com.kiranacademy.rpms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "interview_feedback")
public class InterviewFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Integer feedbackId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false, unique = true)
    private InterviewRound interviewRound;

    @Column(name = "technical_rating", precision = 3, scale = 1)
    private BigDecimal technicalRating;

    @Column(name = "communication_rating", precision = 3, scale = 1)
    private BigDecimal communicationRating;

    @Column(name = "problem_solving_rating", precision = 3, scale = 1)
    private BigDecimal problemSolvingRating;

    @Column(name = "overall_rating", precision = 3, scale = 1)
    private BigDecimal overallRating;

    @Column(name = "recommendation", nullable = false, length = 30)
    private String recommendation;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by")
    private User submittedBy;

    @Column(name = "submitted_at", nullable = false)
    private OffsetDateTime submittedAt = OffsetDateTime.now();

    public InterviewFeedback() {}

    public Integer getFeedbackId() { return feedbackId; }
    public void setFeedbackId(Integer feedbackId) { this.feedbackId = feedbackId; }
    public InterviewRound getInterviewRound() { return interviewRound; }
    public void setInterviewRound(InterviewRound interviewRound) { this.interviewRound = interviewRound; }
    public BigDecimal getTechnicalRating() { return technicalRating; }
    public void setTechnicalRating(BigDecimal technicalRating) { this.technicalRating = technicalRating; }
    public BigDecimal getCommunicationRating() { return communicationRating; }
    public void setCommunicationRating(BigDecimal communicationRating) { this.communicationRating = communicationRating; }
    public BigDecimal getProblemSolvingRating() { return problemSolvingRating; }
    public void setProblemSolvingRating(BigDecimal problemSolvingRating) { this.problemSolvingRating = problemSolvingRating; }
    public BigDecimal getOverallRating() { return overallRating; }
    public void setOverallRating(BigDecimal overallRating) { this.overallRating = overallRating; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public User getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(User submittedBy) { this.submittedBy = submittedBy; }
    public OffsetDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(OffsetDateTime submittedAt) { this.submittedAt = submittedAt; }
}
