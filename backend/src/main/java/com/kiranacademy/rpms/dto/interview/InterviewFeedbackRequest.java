package com.kiranacademy.rpms.dto.interview;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class InterviewFeedbackRequest {
    @DecimalMin("0.0") @DecimalMax("5.0")
    private BigDecimal technicalRating;

    @DecimalMin("0.0") @DecimalMax("5.0")
    private BigDecimal communicationRating;

    @DecimalMin("0.0") @DecimalMax("5.0")
    private BigDecimal problemSolvingRating;

    @DecimalMin("0.0") @DecimalMax("5.0")
    private BigDecimal overallRating;

    @NotBlank(message = "Recommendation is required (NEXT_ROUND, SELECTED, REJECTED, ON_HOLD)")
    private String recommendation;

    private String remarks;

    public InterviewFeedbackRequest() {}

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
}
