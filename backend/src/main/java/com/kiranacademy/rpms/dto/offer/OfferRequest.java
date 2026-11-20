package com.kiranacademy.rpms.dto.offer;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class OfferRequest {
    @NotNull(message = "Application ID is required")
    private Integer applicationId;

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotNull(message = "Offered package is required")
    @DecimalMin(value = "0.0")
    private BigDecimal offeredPackageLpa;

    @NotNull(message = "Offer date is required")
    private LocalDate offerDate;

    private LocalDate expectedJoiningDate;
    private String offerStatus = "OFFERED";
    private String remarks;

    public OfferRequest() {}

    public Integer getApplicationId() { return applicationId; }
    public void setApplicationId(Integer applicationId) { this.applicationId = applicationId; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public BigDecimal getOfferedPackageLpa() { return offeredPackageLpa; }
    public void setOfferedPackageLpa(BigDecimal offeredPackageLpa) { this.offeredPackageLpa = offeredPackageLpa; }
    public LocalDate getOfferDate() { return offerDate; }
    public void setOfferDate(LocalDate offerDate) { this.offerDate = offerDate; }
    public LocalDate getExpectedJoiningDate() { return expectedJoiningDate; }
    public void setExpectedJoiningDate(LocalDate expectedJoiningDate) { this.expectedJoiningDate = expectedJoiningDate; }
    public String getOfferStatus() { return offerStatus; }
    public void setOfferStatus(String offerStatus) { this.offerStatus = offerStatus; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
