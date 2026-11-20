package com.kiranacademy.rpms.dto.placement;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PlacementRequest {
    @NotNull(message = "Offer ID is required")
    private Integer offerId;

    private LocalDate expectedJoiningDate;
    private LocalDate actualJoiningDate;
    private String joiningStatus = "PENDING";
    private String placementStatus = "ACTIVE";

    public PlacementRequest() {}

    public Integer getOfferId() { return offerId; }
    public void setOfferId(Integer offerId) { this.offerId = offerId; }
    public LocalDate getExpectedJoiningDate() { return expectedJoiningDate; }
    public void setExpectedJoiningDate(LocalDate expectedJoiningDate) { this.expectedJoiningDate = expectedJoiningDate; }
    public LocalDate getActualJoiningDate() { return actualJoiningDate; }
    public void setActualJoiningDate(LocalDate actualJoiningDate) { this.actualJoiningDate = actualJoiningDate; }
    public String getJoiningStatus() { return joiningStatus; }
    public void setJoiningStatus(String joiningStatus) { this.joiningStatus = joiningStatus; }
    public String getPlacementStatus() { return placementStatus; }
    public void setPlacementStatus(String placementStatus) { this.placementStatus = placementStatus; }
}
