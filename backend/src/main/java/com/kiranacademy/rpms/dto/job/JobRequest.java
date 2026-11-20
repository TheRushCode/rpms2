package com.kiranacademy.rpms.dto.job;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class JobRequest {
    private String jobCode;

    @NotNull(message = "Company ID is required")
    private Integer companyId;

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    private String employmentType = "FULL_TIME";

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Openings is required")
    @Min(value = 1, message = "Openings must be at least 1")
    private Integer openings;

    private BigDecimal minPackageLpa;
    private BigDecimal maxPackageLpa;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal minPercentage;

    private Integer minPassingYear;
    private Integer maxPassingYear;

    @Min(0)
    private Integer maxBacklogs = 0;

    private BigDecimal experienceMinYears = BigDecimal.ZERO;
    private String qualificationRule;

    @NotNull(message = "Posted date is required")
    private LocalDate postedDate;

    @NotNull(message = "Application deadline is required")
    private LocalDate applicationDeadline;

    private String status = "OPEN";

    public JobRequest() {}

    public String getJobCode() { return jobCode; }
    public void setJobCode(String jobCode) { this.jobCode = jobCode; }
    public Integer getCompanyId() { return companyId; }
    public void setCompanyId(Integer companyId) { this.companyId = companyId; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getOpenings() { return openings; }
    public void setOpenings(Integer openings) { this.openings = openings; }
    public BigDecimal getMinPackageLpa() { return minPackageLpa; }
    public void setMinPackageLpa(BigDecimal minPackageLpa) { this.minPackageLpa = minPackageLpa; }
    public BigDecimal getMaxPackageLpa() { return maxPackageLpa; }
    public void setMaxPackageLpa(BigDecimal maxPackageLpa) { this.maxPackageLpa = maxPackageLpa; }
    public BigDecimal getMinPercentage() { return minPercentage; }
    public void setMinPercentage(BigDecimal minPercentage) { this.minPercentage = minPercentage; }
    public Integer getMinPassingYear() { return minPassingYear; }
    public void setMinPassingYear(Integer minPassingYear) { this.minPassingYear = minPassingYear; }
    public Integer getMaxPassingYear() { return maxPassingYear; }
    public void setMaxPassingYear(Integer maxPassingYear) { this.maxPassingYear = maxPassingYear; }
    public Integer getMaxBacklogs() { return maxBacklogs; }
    public void setMaxBacklogs(Integer maxBacklogs) { this.maxBacklogs = maxBacklogs; }
    public BigDecimal getExperienceMinYears() { return experienceMinYears; }
    public void setExperienceMinYears(BigDecimal experienceMinYears) { this.experienceMinYears = experienceMinYears; }
    public String getQualificationRule() { return qualificationRule; }
    public void setQualificationRule(String qualificationRule) { this.qualificationRule = qualificationRule; }
    public LocalDate getPostedDate() { return postedDate; }
    public void setPostedDate(LocalDate postedDate) { this.postedDate = postedDate; }
    public LocalDate getApplicationDeadline() { return applicationDeadline; }
    public void setApplicationDeadline(LocalDate applicationDeadline) { this.applicationDeadline = applicationDeadline; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
