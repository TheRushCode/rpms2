package com.kiranacademy.rpms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Integer jobId;

    @Column(name = "job_code", nullable = false, unique = true, length = 40)
    private String jobCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "job_title", nullable = false, length = 150)
    private String jobTitle;

    @Column(name = "employment_type", nullable = false, length = 30)
    private String employmentType = "FULL_TIME";

    @Column(name = "location", nullable = false, length = 120)
    private String location;

    @Column(name = "openings", nullable = false)
    private Integer openings;

    @Column(name = "min_package_lpa", precision = 6, scale = 2)
    private BigDecimal minPackageLpa;

    @Column(name = "max_package_lpa", precision = 6, scale = 2)
    private BigDecimal maxPackageLpa;

    @Column(name = "min_percentage", precision = 5, scale = 2)
    private BigDecimal minPercentage;

    @Column(name = "min_passing_year")
    private Integer minPassingYear;

    @Column(name = "max_passing_year")
    private Integer maxPassingYear;

    @Column(name = "max_backlogs", nullable = false)
    private Integer maxBacklogs = 0;

    @Column(name = "experience_min_years", precision = 4, scale = 1, nullable = false)
    private BigDecimal experienceMinYears = BigDecimal.ZERO;

    @Column(name = "qualification_rule", length = 255)
    private String qualificationRule;

    @Column(name = "posted_date", nullable = false)
    private LocalDate postedDate;

    @Column(name = "application_deadline", nullable = false)
    private LocalDate applicationDeadline;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "OPEN";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Job() {}

    public Integer getJobId() { return jobId; }
    public void setJobId(Integer jobId) { this.jobId = jobId; }
    public String getJobCode() { return jobCode; }
    public void setJobCode(String jobCode) { this.jobCode = jobCode; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
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
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
