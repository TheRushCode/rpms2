package com.kiranacademy.rpms;

import com.kiranacademy.rpms.dto.candidate.CandidateRequest;
import com.kiranacademy.rpms.entity.Candidate;
import com.kiranacademy.rpms.exception.BadRequestException;
import com.kiranacademy.rpms.repository.ApplicationRepository;
import com.kiranacademy.rpms.repository.CandidateRepository;
import com.kiranacademy.rpms.service.ActivityLogService;
import com.kiranacademy.rpms.service.CandidateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CandidateServiceTest {

    private Set<String> existingEmails;
    private Set<String> existingMobiles;
    private CandidateRepository candidateRepository;
    private ApplicationRepository applicationRepository;
    private ActivityLogService logService;
    private CandidateService candidateService;

    @BeforeEach
    public void setup() {
        existingEmails = new HashSet<>();
        existingMobiles = new HashSet<>();

        candidateRepository = (CandidateRepository) Proxy.newProxyInstance(
                CandidateRepository.class.getClassLoader(),
                new Class<?>[]{CandidateRepository.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("existsByEmail".equals(name)) {
                        return existingEmails.contains(args[0]);
                    }
                    if ("existsByMobile".equals(name)) {
                        return existingMobiles.contains(args[0]);
                    }
                    if ("save".equals(name)) {
                        Candidate c = (Candidate) args[0];
                        c.setCandidateId(101);
                        return c;
                    }
                    return null;
                }
        );

        applicationRepository = (ApplicationRepository) Proxy.newProxyInstance(
                ApplicationRepository.class.getClassLoader(),
                new Class<?>[]{ApplicationRepository.class},
                (proxy, method, args) -> null
        );

        logService = new ActivityLogService(null) {
            @Override
            public void log(com.kiranacademy.rpms.entity.User user, String module, String action, String recordId, String description) {
                // no-op
            }
        };

        candidateService = new CandidateService(candidateRepository, applicationRepository, logService);
    }

    @Test
    public void testAddCandidateSuccess() {
        CandidateRequest req = new CandidateRequest();
        req.setFullName("John Doe");
        req.setEmail("john.doe@example.com");
        req.setMobile("9876543210");
        req.setQualification("BE");
        req.setPassingYear(2025);
        req.setPercentage(BigDecimal.valueOf(75.5));
        req.setBacklogCount(0);
        req.setPrimaryTechnology("Java");

        Candidate saved = candidateService.addCandidate(req, null);
        assertNotNull(saved);
        assertEquals(101, saved.getCandidateId());
        assertEquals("ACTIVE", saved.getProfileStatus());
        assertEquals("NOT_PLACED", saved.getPlacementStatus());
    }

    @Test
    public void testAddCandidateInvalidMobile() {
        CandidateRequest req = new CandidateRequest();
        req.setFullName("John Doe");
        req.setEmail("john.doe@example.com");
        req.setMobile("12345"); // Invalid mobile length
        req.setPercentage(BigDecimal.valueOf(75.5));
        req.setBacklogCount(0);

        assertThrows(BadRequestException.class, () -> candidateService.addCandidate(req, null));
    }

    @Test
    public void testAddCandidateDuplicateEmail() {
        existingEmails.add("duplicate@example.com");

        CandidateRequest req = new CandidateRequest();
        req.setFullName("John Doe");
        req.setEmail("duplicate@example.com");
        req.setMobile("9876543210");
        req.setPercentage(BigDecimal.valueOf(75.5));
        req.setBacklogCount(0);

        assertThrows(BadRequestException.class, () -> candidateService.addCandidate(req, null));
    }

    @Test
    public void testAddCandidateDuplicateMobile() {
        existingMobiles.add("9876543210");

        CandidateRequest req = new CandidateRequest();
        req.setFullName("John Doe");
        req.setEmail("unique@example.com");
        req.setMobile("9876543210");
        req.setPercentage(BigDecimal.valueOf(75.5));
        req.setBacklogCount(0);

        assertThrows(BadRequestException.class, () -> candidateService.addCandidate(req, null));
    }
}
