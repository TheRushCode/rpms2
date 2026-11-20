package com.kiranacademy.rpms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "offers")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offer_id")
    private Integer offerId;

    @Column(name = "offer_code", nullable = false, unique = true, length = 40)
    private String offerCode;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private Application application;

    @Column(name = "designation", nullable = false, length = 120)
    private String designation;

    @Column(name = "offered_package_lpa", precision = 6, scale = 2, nullable = false)
    private BigDecimal offeredPackageLpa;

    @Column(name = "offer_date", nullable = false)
    private LocalDate offerDate;

    @Column(name = "expected_joining_date")
    private LocalDate expectedJoiningDate;

    @Column(name = "offer_status", nullable = false, length = 20)
    private String offerStatus = "OFFERED";

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Offer() {}

    public Integer getOfferId() { return offerId; }
    public void setOfferId(Integer offerId) { this.offerId = offerId; }
    public String getOfferCode() { return offerCode; }
    public void setOfferCode(String offerCode) { this.offerCode = offerCode; }
    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }
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
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
