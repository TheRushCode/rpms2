package com.kiranacademy.rpms.service;

import com.kiranacademy.rpms.dto.job.JobRequest;
import com.kiranacademy.rpms.entity.Company;
import com.kiranacademy.rpms.entity.Job;
import com.kiranacademy.rpms.entity.User;
import com.kiranacademy.rpms.exception.BadRequestException;
import com.kiranacademy.rpms.exception.JobNotFoundException;
import com.kiranacademy.rpms.repository.CompanyRepository;
import com.kiranacademy.rpms.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final ActivityLogService logService;

    public JobService(JobRepository jobRepository,
                      CompanyRepository companyRepository,
                      ActivityLogService logService) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.logService = logService;
    }

    @Transactional
    public Job createJob(JobRequest req, User currentUser) {
        Company company = companyRepository.findById(req.getCompanyId())
                .orElseThrow(() -> new BadRequestException("Company ID " + req.getCompanyId() + " not found"));

        if (req.getApplicationDeadline().isBefore(req.getPostedDate())) {
            throw new BadRequestException("Application deadline cannot be before posted date");
        }

        String jobCode = req.getJobCode();
        if (jobCode == null || jobCode.isBlank()) {
            jobCode = "JOB-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        } else if (jobRepository.existsByJobCode(jobCode)) {
            throw new BadRequestException("Job code already exists");
        }

        Job j = new Job();
        j.setJobCode(jobCode);
        j.setCompany(company);
        j.setJobTitle(req.getJobTitle());
        j.setEmploymentType(req.getEmploymentType() != null ? req.getEmploymentType() : "FULL_TIME");
        j.setLocation(req.getLocation());
        j.setOpenings(req.getOpenings());
        j.setMinPackageLpa(req.getMinPackageLpa());
        j.setMaxPackageLpa(req.getMaxPackageLpa());
        j.setMinPercentage(req.getMinPercentage());
        j.setMinPassingYear(req.getMinPassingYear());
        j.setMaxPassingYear(req.getMaxPassingYear());
        j.setMaxBacklogs(req.getMaxBacklogs() != null ? req.getMaxBacklogs() : 0);
        j.setExperienceMinYears(req.getExperienceMinYears());
        j.setQualificationRule(req.getQualificationRule());
        j.setPostedDate(req.getPostedDate());
        j.setApplicationDeadline(req.getApplicationDeadline());
        j.setStatus(req.getStatus() != null ? req.getStatus() : "OPEN");
        j.setCreatedBy(currentUser);

        Job saved = jobRepository.save(j);
        logService.log(currentUser, "JOB", "CREATE", saved.getJobCode(), "Created job opening: " + saved.getJobTitle());
        return saved;
    }

    @Transactional
    public Job updateJob(int id, JobRequest req, User currentUser) {
        Job j = getJob(id);
        Company company = companyRepository.findById(req.getCompanyId())
                .orElseThrow(() -> new BadRequestException("Company ID " + req.getCompanyId() + " not found"));

        if (req.getApplicationDeadline().isBefore(req.getPostedDate())) {
            throw new BadRequestException("Application deadline cannot be before posted date");
        }

        j.setCompany(company);
        j.setJobTitle(req.getJobTitle());
        j.setEmploymentType(req.getEmploymentType());
        j.setLocation(req.getLocation());
        j.setOpenings(req.getOpenings());
        j.setMinPackageLpa(req.getMinPackageLpa());
        j.setMaxPackageLpa(req.getMaxPackageLpa());
        j.setMinPercentage(req.getMinPercentage());
        j.setMinPassingYear(req.getMinPassingYear());
        j.setMaxPassingYear(req.getMaxPassingYear());
        j.setMaxBacklogs(req.getMaxBacklogs());
        j.setExperienceMinYears(req.getExperienceMinYears());
        j.setQualificationRule(req.getQualificationRule());
        j.setPostedDate(req.getPostedDate());
        j.setApplicationDeadline(req.getApplicationDeadline());
        if (req.getStatus() != null) j.setStatus(req.getStatus());

        Job updated = jobRepository.save(j);
        logService.log(currentUser, "JOB", "UPDATE", updated.getJobCode(), "Updated job: " + updated.getJobTitle());
        return updated;
    }

    public Job getJob(int id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job ID " + id + " not found"));
    }

    public Page<Job> getJobs(String search, String status, Integer companyId, Pageable pageable) {
        return jobRepository.filterJobs(search, status, companyId, pageable);
    }

    public List<Job> getOpenJobs() {
        return jobRepository.findByStatus("OPEN");
    }

    @Transactional
    public void updateStatus(int id, String status, User currentUser) {
        Job j = getJob(id);
        j.setStatus(status);
        jobRepository.save(j);
        logService.log(currentUser, "JOB", "STATUS_CHANGE", j.getJobCode(), "Changed status to: " + status);
    }
}
