package com.kiranacademy.rpms.dto.company;

import jakarta.validation.constraints.NotBlank;

public class CompanyRequest {
    private String companyCode;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String industry;
    private String website;
    private String location;
    private String companyType;
    private String hrName;
    private String hrEmail;
    private String hrMobile;
    private String status = "ACTIVE";

    public CompanyRequest() {}

    public String getCompanyCode() { return companyCode; }
    public void setCompanyCode(String companyCode) { this.companyCode = companyCode; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCompanyType() { return companyType; }
    public void setCompanyType(String companyType) { this.companyType = companyType; }
    public String getHrName() { return hrName; }
    public void setHrName(String hrName) { this.hrName = hrName; }
    public String getHrEmail() { return hrEmail; }
    public void setHrEmail(String hrEmail) { this.hrEmail = hrEmail; }
    public String getHrMobile() { return hrMobile; }
    public void setHrMobile(String hrMobile) { this.hrMobile = hrMobile; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
