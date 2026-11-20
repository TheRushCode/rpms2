package com.kiranacademy.rpms.service;

import com.kiranacademy.rpms.dto.application.ApplicationRequest;
import com.kiranacademy.rpms.entity.Application;
import com.kiranacademy.rpms.entity.ApplicationHistory;
import com.kiranacademy.rpms.entity.Candidate;
import com.kiranacademy.rpms.entity.Job;
import com.kiranacademy.rpms.entity.User;
import com.kiranacademy.rpms.exception.CandidateNotEligibleException;
import com.kiranacademy.rpms.exception.CandidateNotFoundException;
import com.kiranacademy.rpms.exception.DuplicateApplicationException;
import com.kiranacademy.rpms.exception.JobNotFoundException;
import com.kiranacademy.rpms.exception.ResourceNotFoundException;
import com.kiranacademy.rpms.repository.ApplicationHistoryRepository;
import com.kiranacademy.rpms.repository.ApplicationRepository;
import com.kiranacademy.rpms.repository.CandidateRepository;
import com.kiranacademy.rpms.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationHistoryRepository historyRepository;
    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;
    private final EligibilityService eligibilityService;
    private final ActivityLogService logService;

    public ApplicationService(ApplicationRepository applicationRepository,
                              ApplicationHistoryRepository historyRepository,
                              CandidateRepository candidateRepository,
                              JobRepository jobRepository,
                              EligibilityService eligibilityService,
                              ActivityLogService logService) {
        this.applicationRepository = applicationRepository;
        this.historyRepository = historyRepository;
        this.candidateRepository = candidateRepository;
        this.jobRepository = jobRepository;
        this.eligibilityService = eligibilityService;
        this.logService = logService;
    }

    @Transactional
    public Application applyForJob(ApplicationRequest req, User currentUser) {
        Candidate candidate = candidateRepository.findById(req.getCandidateId())
                .orElseThrow(() -> new CandidateNotFoundException("Candidate ID " + req.getCandidateId() + " not found"));

        Job job = jobRepository.findById(req.getJobId())
                .orElseThrow(() -> new JobNotFoundException("Job ID " + req.getJobId() + " not found"));

        // Check duplicate application
        if (applicationRepository.existsByCandidateCandidateIdAndJobJobId(candidate.getCandidateId(), job.getJobId())) {
            throw new DuplicateApplicationException("Candidate has already applied for this job");
        }

        // Run eligibility service rules
        List<String> reasons = eligibilityService.getFailureReasons(candidate, job);
        if (!reasons.isEmpty()) {
            throw new CandidateNotEligibleException(String.join("; ", reasons));
        }

        Application app = new Application();
        app.setApplicationCode("APP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        app.setCandidate(candidate);
        app.setJob(job);
        app.setEligibilityStatus("ELIGIBLE");
        app.setEligibilityReason("Meets eligibility criteria");
        app.setApplicationStatus("APPLIED");
        app.setSource(req.getSource() != null ? req.getSource() : "PORTAL");
        app.setAssignedRecruiter(currentUser);

        Application saved = applicationRepository.save(app);

        // Record history
        ApplicationHistory history = new ApplicationHistory();
        history.setApplication(saved);
        history.setOldStatus(null);
        history.setNewStatus("APPLIED");
        history.setRemarks("Application submitted");
        history.setChangedBy(currentUser);
        historyRepository.save(history);

        logService.log(currentUser, "APPLICATION", "APPLY", saved.getApplicationCode(),
                "Candidate " + candidate.getFullName() + " applied for job " + job.getJobTitle());

        return saved;
    }

    public Map<String, Object> checkEligibility(int candidateId, int jobId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new CandidateNotFoundException("Candidate ID " + candidateId + " not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job ID " + jobId + " not found"));

        List<String> failureReasons = eligibilityService.getFailureReasons(candidate, job);
        boolean alreadyApplied = applicationRepository.existsByCandidateCandidateIdAndJobJobId(candidateId, jobId);

        Map<String, Object> result = new HashMap<>();
        result.put("candidateId", candidateId);
        result.put("candidateName", candidate.getFullName());
        result.put("jobId", jobId);
        result.put("jobTitle", job.getJobTitle());
        result.put("eligible", failureReasons.isEmpty() && !alreadyApplied);
        result.put("alreadyApplied", alreadyApplied);
        result.put("reasons", failureReasons);
        return result;
    }

    @Transactional
    public Application updateStatus(int applicationId, String newStatus, String remarks, User currentUser) {
        Application app = getApplication(applicationId);
        String oldStatus = app.getApplicationStatus();

        app.setApplicationStatus(newStatus);
        Application updated = applicationRepository.save(app);

        ApplicationHistory history = new ApplicationHistory();
        history.setApplication(updated);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setRemarks(remarks != null ? remarks : "Status updated from " + oldStatus + " to " + newStatus);
        history.setChangedBy(currentUser);
        historyRepository.save(history);

        logService.log(currentUser, "APPLICATION", "STATUS_CHANGE", updated.getApplicationCode(),
                "Status changed from " + oldStatus + " to " + newStatus);

        return updated;
    }

    public Application getApplication(int id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application ID " + id + " not found"));
    }

    public Page<Application> getApplications(String search, String status, String eligibility, Integer jobId, Integer candidateId, Pageable pageable) {
        return applicationRepository.filterApplications(search, status, eligibility, jobId, candidateId, pageable);
    }

    public List<Application> getCandidateApplications(int candidateId) {
        return applicationRepository.findByCandidateCandidateId(candidateId);
    }

    public List<ApplicationHistory> getApplicationHistory(int applicationId) {
        return historyRepository.findByApplicationApplicationIdOrderByChangedAtDesc(applicationId);
    }
}
