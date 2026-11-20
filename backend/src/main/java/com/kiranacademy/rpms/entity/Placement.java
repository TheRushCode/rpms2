package com.kiranacademy.rpms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "placements")
public class Placement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "placement_id")
    private Integer placementId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "offer_id", nullable = false, unique = true)
    private Offer offer;

    @Column(name = "designation", nullable = false, length = 120)
    private String designation;

    @Column(name = "final_package_lpa", precision = 6, scale = 2, nullable = false)
    private BigDecimal finalPackageLpa;

    @Column(name = "expected_joining_date")
    private LocalDate expectedJoiningDate;

    @Column(name = "actual_joining_date")
    private LocalDate actualJoiningDate;

    @Column(name = "joining_status", nullable = false, length = 20)
    private String joiningStatus = "PENDING";

    @Column(name = "placement_status", nullable = false, length = 20)
    private String placementStatus = "ACTIVE";

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Placement() {}

    public Integer getPlacementId() { return placementId; }
    public void setPlacementId(Integer placementId) { this.placementId = placementId; }
    public Candidate getCandidate() { return candidate; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
    public Offer getOffer() { return offer; }
    public void setOffer(Offer offer) { this.offer = offer; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public BigDecimal getFinalPackageLpa() { return finalPackageLpa; }
    public void setFinalPackageLpa(BigDecimal finalPackageLpa) { this.finalPackageLpa = finalPackageLpa; }
    public LocalDate getExpectedJoiningDate() { return expectedJoiningDate; }
    public void setExpectedJoiningDate(LocalDate expectedJoiningDate) { this.expectedJoiningDate = expectedJoiningDate; }
    public LocalDate getActualJoiningDate() { return actualJoiningDate; }
    public void setActualJoiningDate(LocalDate actualJoiningDate) { this.actualJoiningDate = actualJoiningDate; }
    public String getJoiningStatus() { return joiningStatus; }
    public void setJoiningStatus(String joiningStatus) { this.joiningStatus = joiningStatus; }
    public String getPlacementStatus() { return placementStatus; }
    public void setPlacementStatus(String placementStatus) { this.placementStatus = placementStatus; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
