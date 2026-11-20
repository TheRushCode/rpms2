package com.kiranacademy.rpms.dto.candidate;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CandidateRequest {
    private String candidateCode;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Mobile is required")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile must contain exactly 10 digits")
    private String mobile;

    private LocalDate dateOfBirth;
    private String gender;

    @NotBlank(message = "Qualification is required")
    private String qualification;

    private String college;
    private String university;

    @NotNull(message = "Passing year is required")
    @Min(value = 1990, message = "Passing year must be valid")
    @Max(value = 2035, message = "Passing year must be valid")
    private Integer passingYear;

    @NotNull(message = "Percentage is required")
    @DecimalMin(value = "0.0", message = "Percentage must be 0-100")
    @DecimalMax(value = "100.0", message = "Percentage must be 0-100")
    private BigDecimal percentage;

    private BigDecimal cgpa;

    @NotNull(message = "Backlog count is required")
    @Min(value = 0, message = "Backlog count cannot be negative")
    private Integer backlogCount;

    @NotBlank(message = "Primary technology is required")
    private String primaryTechnology;

    private String currentLocation;
    private String preferredLocation;
    private String profileStatus = "ACTIVE";
    private String placementStatus = "NOT_PLACED";

    public CandidateRequest() {}

    public String getCandidateCode() { return candidateCode; }
    public void setCandidateCode(String candidateCode) { this.candidateCode = candidateCode; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public Integer getPassingYear() { return passingYear; }
    public void setPassingYear(Integer passingYear) { this.passingYear = passingYear; }
    public BigDecimal getPercentage() { return percentage; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
    public BigDecimal getCgpa() { return cgpa; }
    public void setCgpa(BigDecimal cgpa) { this.cgpa = cgpa; }
    public Integer getBacklogCount() { return backlogCount; }
    public void setBacklogCount(Integer backlogCount) { this.backlogCount = backlogCount; }
    public String getPrimaryTechnology() { return primaryTechnology; }
    public void setPrimaryTechnology(String primaryTechnology) { this.primaryTechnology = primaryTechnology; }
    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
    public String getPreferredLocation() { return preferredLocation; }
    public void setPreferredLocation(String preferredLocation) { this.preferredLocation = preferredLocation; }
    public String getProfileStatus() { return profileStatus; }
    public void setProfileStatus(String profileStatus) { this.profileStatus = profileStatus; }
    public String getPlacementStatus() { return placementStatus; }
    public void setPlacementStatus(String placementStatus) { this.placementStatus = placementStatus; }
}
