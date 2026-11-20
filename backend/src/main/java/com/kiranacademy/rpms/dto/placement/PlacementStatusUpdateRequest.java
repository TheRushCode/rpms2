package com.kiranacademy.rpms.dto.placement;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class PlacementStatusUpdateRequest {
    @NotBlank(message = "Joining status is required (PENDING, JOINED, NOT_JOINED, DEFERRED, NO_SHOW)")
    private String joiningStatus;

    private LocalDate actualJoiningDate;

    public PlacementStatusUpdateRequest() {}

    public String getJoiningStatus() { return joiningStatus; }
    public void setJoiningStatus(String joiningStatus) { this.joiningStatus = joiningStatus; }
    public LocalDate getActualJoiningDate() { return actualJoiningDate; }
    public void setActualJoiningDate(LocalDate actualJoiningDate) { this.actualJoiningDate = actualJoiningDate; }
}
