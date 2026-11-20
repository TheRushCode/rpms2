package com.kiranacademy.rpms.service;

import com.kiranacademy.rpms.dto.company.CompanyRequest;
import com.kiranacademy.rpms.entity.Company;
import com.kiranacademy.rpms.entity.User;
import com.kiranacademy.rpms.exception.BadRequestException;
import com.kiranacademy.rpms.exception.ResourceNotFoundException;
import com.kiranacademy.rpms.repository.CompanyRepository;
import com.kiranacademy.rpms.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final ActivityLogService logService;

    public CompanyService(CompanyRepository companyRepository,
                          JobRepository jobRepository,
                          ActivityLogService logService) {
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
        this.logService = logService;
    }

    @Transactional
    public Company createCompany(CompanyRequest req, User currentUser) {
        String companyCode = req.getCompanyCode();
        if (companyCode == null || companyCode.isBlank()) {
            companyCode = "COM-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        } else if (companyRepository.existsByCompanyCode(companyCode)) {
            throw new BadRequestException("Company code already exists");
        }

        Company c = new Company();
        c.setCompanyCode(companyCode);
        c.setCompanyName(req.getCompanyName());
        c.setIndustry(req.getIndustry());
        c.setWebsite(req.getWebsite());
        c.setLocation(req.getLocation());
        c.setCompanyType(req.getCompanyType());
        c.setHrName(req.getHrName());
        c.setHrEmail(req.getHrEmail());
        c.setHrMobile(req.getHrMobile());
        c.setStatus(req.getStatus() != null ? req.getStatus() : "ACTIVE");

        Company saved = companyRepository.save(c);
        logService.log(currentUser, "COMPANY", "CREATE", saved.getCompanyCode(), "Added company: " + saved.getCompanyName());
        return saved;
    }

    @Transactional
    public Company updateCompany(int id, CompanyRequest req, User currentUser) {
        Company c = getCompany(id);
        c.setCompanyName(req.getCompanyName());
        c.setIndustry(req.getIndustry());
        c.setWebsite(req.getWebsite());
        c.setLocation(req.getLocation());
        c.setCompanyType(req.getCompanyType());
        c.setHrName(req.getHrName());
        c.setHrEmail(req.getHrEmail());
        c.setHrMobile(req.getHrMobile());
        if (req.getStatus() != null) c.setStatus(req.getStatus());

        Company updated = companyRepository.save(c);
        logService.log(currentUser, "COMPANY", "UPDATE", updated.getCompanyCode(), "Updated company: " + updated.getCompanyName());
        return updated;
    }

    public Company getCompany(int id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company ID " + id + " not found"));
    }

    public Page<Company> getCompanies(String search, String status, Pageable pageable) {
        return companyRepository.filterCompanies(search, status, pageable);
    }

    public List<Company> getActiveCompanies() {
        return companyRepository.findByStatus("ACTIVE");
    }

    @Transactional
    public void deleteCompany(int id, User currentUser) {
        Company c = getCompany(id);
        if (!jobRepository.findByCompanyCompanyId(id).isEmpty()) {
            throw new BadRequestException("Cannot delete company with associated jobs. Set status to INACTIVE instead.");
        }
        companyRepository.delete(c);
        logService.log(currentUser, "COMPANY", "DELETE", c.getCompanyCode(), "Deleted company: " + c.getCompanyName());
    }
}
