package com.kiranacademy.rpms;

import com.kiranacademy.rpms.entity.Candidate;
import com.kiranacademy.rpms.entity.Job;
import com.kiranacademy.rpms.service.EligibilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EligibilityServiceTest {

    private EligibilityService eligibilityService;

    @BeforeEach
    public void setup() {
        eligibilityService = new EligibilityService();
    }

    private Candidate createCandidate(String status, double percentage, int passingYear, int backlogs) {
        Candidate c = new Candidate();
        c.setCandidateCode("CAN-TEST");
        c.setFullName("Test Candidate");
        c.setEmail("candidate@test.com");
        c.setMobile("9876543210");
        c.setQualification("BE");
        c.setProfileStatus(status);
        c.setPercentage(BigDecimal.valueOf(percentage));
        c.setPassingYear(passingYear);
        c.setBacklogCount(backlogs);
        c.setPrimaryTechnology("Java");
        return c;
    }

    private Job createJob(String status, double minPercentage, int minYear, int maxYear, int maxBacklogs, LocalDate deadline) {
        Job j = new Job();
        j.setJobCode("JOB-TEST");
        j.setJobTitle("Software Engineer");
        j.setStatus(status);
        j.setMinPercentage(BigDecimal.valueOf(minPercentage));
        j.setMinPassingYear(minYear);
        j.setMaxPassingYear(maxYear);
        j.setMaxBacklogs(maxBacklogs);
        j.setPostedDate(LocalDate.now().minusDays(5));
        j.setApplicationDeadline(deadline);
        return j;
    }

    @Test
    public void testEligibleCandidate() {
        Candidate candidate = createCandidate("ACTIVE", 75.0, 2025, 0);
        Job job = createJob("OPEN", 60.0, 2024, 2026, 0, LocalDate.now().plusDays(10));

        assertTrue(eligibilityService.isEligible(candidate, job));
        assertTrue(eligibilityService.getFailureReasons(candidate, job).isEmpty());
    }

    @Test
    public void testInactiveCandidateProfile() {
        Candidate candidate = createCandidate("INACTIVE", 75.0, 2025, 0);
        Job job = createJob("OPEN", 60.0, 2024, 2026, 0, LocalDate.now().plusDays(10));

        assertFalse(eligibilityService.isEligible(candidate, job));
        List<String> reasons = eligibilityService.getFailureReasons(candidate, job);
        assertTrue(reasons.stream().anyMatch(r -> r.contains("not ACTIVE")));
    }

    @Test
    public void testJobNotOpen() {
        Candidate candidate = createCandidate("ACTIVE", 75.0, 2025, 0);
        Job job = createJob("CLOSED", 60.0, 2024, 2026, 0, LocalDate.now().plusDays(10));

        assertFalse(eligibilityService.isEligible(candidate, job));
        List<String> reasons = eligibilityService.getFailureReasons(candidate, job);
        assertTrue(reasons.stream().anyMatch(r -> r.contains("not OPEN")));
    }

    @Test
    public void testExpiredDeadline() {
        Candidate candidate = createCandidate("ACTIVE", 75.0, 2025, 0);
        Job job = createJob("OPEN", 60.0, 2024, 2026, 0, LocalDate.now().minusDays(1));

        assertFalse(eligibilityService.isEligible(candidate, job));
        List<String> reasons = eligibilityService.getFailureReasons(candidate, job);
        assertTrue(reasons.stream().anyMatch(r -> r.contains("deadline has passed")));
    }

    @Test
    public void testPercentageBelowRequirement() {
        Candidate candidate = createCandidate("ACTIVE", 55.0, 2025, 0);
        Job job = createJob("OPEN", 60.0, 2024, 2026, 0, LocalDate.now().plusDays(10));

        assertFalse(eligibilityService.isEligible(candidate, job));
        List<String> reasons = eligibilityService.getFailureReasons(candidate, job);
        assertTrue(reasons.stream().anyMatch(r -> r.contains("Percentage below minimum")));
    }

    @Test
    public void testBacklogExceedsLimit() {
        Candidate candidate = createCandidate("ACTIVE", 75.0, 2025, 2);
        Job job = createJob("OPEN", 60.0, 2024, 2026, 0, LocalDate.now().plusDays(10));

        assertFalse(eligibilityService.isEligible(candidate, job));
        List<String> reasons = eligibilityService.getFailureReasons(candidate, job);
        assertTrue(reasons.stream().anyMatch(r -> r.contains("Backlog count")));
    }

    @Test
    public void testPassingYearOutOfRange() {
        Candidate candidateOld = createCandidate("ACTIVE", 75.0, 2022, 0);
        Job job = createJob("OPEN", 60.0, 2024, 2026, 0, LocalDate.now().plusDays(10));

        assertFalse(eligibilityService.isEligible(candidateOld, job));
        List<String> reasons = eligibilityService.getFailureReasons(candidateOld, job);
        assertTrue(reasons.stream().anyMatch(r -> r.contains("below allowed minimum")));

        Candidate candidateFuture = createCandidate("ACTIVE", 75.0, 2028, 0);
        assertFalse(eligibilityService.isEligible(candidateFuture, job));
        List<String> reasons2 = eligibilityService.getFailureReasons(candidateFuture, job);
        assertTrue(reasons2.stream().anyMatch(r -> r.contains("above allowed maximum")));
    }
}
