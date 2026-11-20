package com.kiranacademy.rpms.dto.application;

import jakarta.validation.constraints.NotBlank;

public class ApplicationStatusUpdateRequest {
    @NotBlank(message = "New status is required")
    private String newStatus;

    private String remarks;

    public ApplicationStatusUpdateRequest() {}

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
