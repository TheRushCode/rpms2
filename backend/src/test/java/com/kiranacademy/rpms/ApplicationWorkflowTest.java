package com.kiranacademy.rpms;

import com.kiranacademy.rpms.dto.application.ApplicationRequest;
import com.kiranacademy.rpms.entity.Application;
import com.kiranacademy.rpms.entity.Candidate;
import com.kiranacademy.rpms.entity.Job;
import com.kiranacademy.rpms.exception.CandidateNotEligibleException;
import com.kiranacademy.rpms.exception.DuplicateApplicationException;
import com.kiranacademy.rpms.repository.ApplicationHistoryRepository;
import com.kiranacademy.rpms.repository.ApplicationRepository;
import com.kiranacademy.rpms.repository.CandidateRepository;
import com.kiranacademy.rpms.repository.JobRepository;
import com.kiranacademy.rpms.service.ActivityLogService;
import com.kiranacademy.rpms.service.ApplicationService;
import com.kiranacademy.rpms.service.EligibilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationWorkflowTest {

    private Map<Integer, Candidate> candidateMap;
    private Map<Integer, Job> jobMap;
    private Set<String> candidateJobPairs;

    private ApplicationRepository applicationRepository;
    private ApplicationHistoryRepository historyRepository;
    private CandidateRepository candidateRepository;
    private JobRepository jobRepository;
    private EligibilityService eligibilityService;
    private ActivityLogService logService;
    private ApplicationService applicationService;

    @BeforeEach
    public void setup() {
        candidateMap = new HashMap<>();
        jobMap = new HashMap<>();
        candidateJobPairs = new HashSet<>();

        candidateRepository = (CandidateRepository) Proxy.newProxyInstance(
                CandidateRepository.class.getClassLoader(),
                new Class<?>[]{CandidateRepository.class},
                (proxy, method, args) -> {
                    if ("findById".equals(method.getName())) {
                        return Optional.ofNullable(candidateMap.get(args[0]));
                    }
                    return null;
                }
        );

        jobRepository = (JobRepository) Proxy.newProxyInstance(
                JobRepository.class.getClassLoader(),
                new Class<?>[]{JobRepository.class},
                (proxy, method, args) -> {
                    if ("findById".equals(method.getName())) {
                        return Optional.ofNullable(jobMap.get(args[0]));
                    }
                    return null;
                }
        );

        applicationRepository = (ApplicationRepository) Proxy.newProxyInstance(
                ApplicationRepository.class.getClassLoader(),
                new Class<?>[]{ApplicationRepository.class},
                (proxy, method, args) -> {
                    if ("existsByCandidateCandidateIdAndJobJobId".equals(method.getName())) {
                        return candidateJobPairs.contains(args[0] + "-" + args[1]);
                    }
                    if ("save".equals(method.getName())) {
                        Application a = (Application) args[0];
                        a.setApplicationId(501);
                        return a;
                    }
                    return null;
                }
        );

        historyRepository = (ApplicationHistoryRepository) Proxy.newProxyInstance(
                ApplicationHistoryRepository.class.getClassLoader(),
                new Class<?>[]{ApplicationHistoryRepository.class},
                (proxy, method, args) -> args != null && args.length > 0 ? args[0] : null
        );

        logService = new ActivityLogService(null) {
            @Override
            public void log(com.kiranacademy.rpms.entity.User user, String module, String action, String recordId, String description) {
                // no-op
            }
        };

        eligibilityService = new EligibilityService();

        applicationService = new ApplicationService(
                applicationRepository, historyRepository, candidateRepository, jobRepository,
                eligibilityService, logService
        );
    }

    private Candidate createCandidate(int id) {
        Candidate c = new Candidate();
        c.setCandidateId(id);
        c.setCandidateCode("CAN-00" + id);
        c.setFullName("Candidate " + id);
        c.setProfileStatus("ACTIVE");
        c.setPercentage(BigDecimal.valueOf(70.0));
        c.setPassingYear(2025);
        c.setBacklogCount(0);
        return c;
    }

    private Job createJob(int id) {
        Job j = new Job();
        j.setJobId(id);
        j.setJobCode("JOB-00" + id);
        j.setJobTitle("Software Engineer");
        j.setStatus("OPEN");
        j.setMinPercentage(BigDecimal.valueOf(60.0));
        j.setMinPassingYear(2024);
        j.setMaxPassingYear(2026);
        j.setMaxBacklogs(0);
        j.setPostedDate(LocalDate.now().minusDays(5));
        j.setApplicationDeadline(LocalDate.now().plusDays(10));
        return j;
    }

    @Test
    public void testSuccessfulApplication() {
        Candidate candidate = createCandidate(1);
        Job job = createJob(1);
        candidateMap.put(1, candidate);
        jobMap.put(1, job);

        Application app = applicationService.applyForJob(new ApplicationRequest(1, 1), null);
        assertNotNull(app);
        assertEquals(501, app.getApplicationId());
        assertEquals("ELIGIBLE", app.getEligibilityStatus());
        assertEquals("APPLIED", app.getApplicationStatus());
        assertTrue(app.getApplicationCode().startsWith("APP-"));
    }

    @Test
    public void testDuplicateApplicationPrevented() {
        Candidate candidate = createCandidate(1);
        Job job = createJob(1);
        candidateMap.put(1, candidate);
        jobMap.put(1, job);
        candidateJobPairs.add("1-1");

        assertThrows(DuplicateApplicationException.class, () ->
                applicationService.applyForJob(new ApplicationRequest(1, 1), null));
    }

    @Test
    public void testIneligibleApplicationPrevented() {
        Candidate candidate = createCandidate(1);
        candidate.setPercentage(BigDecimal.valueOf(50.0)); // Below minimum requirement 60.0
        Job job = createJob(1);
        candidateMap.put(1, candidate);
        jobMap.put(1, job);

        assertThrows(CandidateNotEligibleException.class, () ->
                applicationService.applyForJob(new ApplicationRequest(1, 1), null));
    }
}
