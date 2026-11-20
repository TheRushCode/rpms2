package com.kiranacademy.rpms.service;

import com.kiranacademy.rpms.dto.candidate.CandidateRequest;
import com.kiranacademy.rpms.entity.Candidate;
import com.kiranacademy.rpms.entity.User;
import com.kiranacademy.rpms.exception.BadRequestException;
import com.kiranacademy.rpms.exception.CandidateNotFoundException;
import com.kiranacademy.rpms.repository.ApplicationRepository;
import com.kiranacademy.rpms.repository.CandidateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final ApplicationRepository applicationRepository;
    private final ActivityLogService logService;

    public CandidateService(CandidateRepository candidateRepository,
                            ApplicationRepository applicationRepository,
                            ActivityLogService logService) {
        this.candidateRepository = candidateRepository;
        this.applicationRepository = applicationRepository;
        this.logService = logService;
    }

    @Transactional
    public Candidate addCandidate(CandidateRequest req, User currentUser) {
        // Enforce existing validations from ValidationUtil and CandidateService
        if (req.getEmail() == null || !req.getEmail().contains("@") || !req.getEmail().contains(".")) {
            throw new BadRequestException("Invalid email format");
        }

        if (req.getMobile() == null || !req.getMobile().matches("\\d{10}")) {
            throw new BadRequestException("Mobile must contain exactly 10 digits");
        }

        if (req.getPercentage() != null && (req.getPercentage().doubleValue() < 0 || req.getPercentage().doubleValue() > 100)) {
            throw new BadRequestException("Percentage must be 0-100");
        }

        if (req.getBacklogCount() != null && req.getBacklogCount() < 0) {
            throw new BadRequestException("Backlog count cannot be negative");
        }

        if (candidateRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Duplicate email: Candidate with this email already exists");
        }

        if (candidateRepository.existsByMobile(req.getMobile())) {
            throw new BadRequestException("Duplicate mobile: Candidate with this mobile number already exists");
        }

        String candidateCode = req.getCandidateCode();
        if (candidateCode == null || candidateCode.isBlank()) {
            candidateCode = "CAN-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        }

        Candidate c = new Candidate();
        c.setCandidateCode(candidateCode);
        c.setFullName(req.getFullName());
        c.setEmail(req.getEmail());
        c.setMobile(req.getMobile());
        c.setDateOfBirth(req.getDateOfBirth());
        c.setGender(req.getGender());
        c.setQualification(req.getQualification());
        c.setCollege(req.getCollege());
        c.setUniversity(req.getUniversity());
        c.setPassingYear(req.getPassingYear());
        c.setPercentage(req.getPercentage());
        c.setCgpa(req.getCgpa());
        c.setBacklogCount(req.getBacklogCount() != null ? req.getBacklogCount() : 0);
        c.setPrimaryTechnology(req.getPrimaryTechnology());
        c.setCurrentLocation(req.getCurrentLocation());
        c.setPreferredLocation(req.getPreferredLocation());
        c.setProfileStatus(req.getProfileStatus() != null ? req.getProfileStatus() : "ACTIVE");
        c.setPlacementStatus("NOT_PLACED");

        Candidate saved = candidateRepository.save(c);
        logService.log(currentUser, "CANDIDATE", "CREATE", saved.getCandidateCode(), "Registered candidate: " + saved.getFullName());
        return saved;
    }

    @Transactional
    public Candidate updateCandidate(Integer id, CandidateRequest req, User currentUser) {
        Candidate c = getCandidate(id);

        if (!c.getEmail().equalsIgnoreCase(req.getEmail()) && candidateRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Duplicate email: another candidate is using this email");
        }

        if (!c.getMobile().equals(req.getMobile()) && candidateRepository.existsByMobile(req.getMobile())) {
            throw new BadRequestException("Duplicate mobile: another candidate is using this mobile");
        }

        c.setFullName(req.getFullName());
        c.setEmail(req.getEmail());
        c.setMobile(req.getMobile());
        c.setDateOfBirth(req.getDateOfBirth());
        c.setGender(req.getGender());
        c.setQualification(req.getQualification());
        c.setCollege(req.getCollege());
        c.setUniversity(req.getUniversity());
        c.setPassingYear(req.getPassingYear());
        c.setPercentage(req.getPercentage());
        c.setCgpa(req.getCgpa());
        c.setBacklogCount(req.getBacklogCount());
        c.setPrimaryTechnology(req.getPrimaryTechnology());
        c.setCurrentLocation(req.getCurrentLocation());
        c.setPreferredLocation(req.getPreferredLocation());
        if (req.getProfileStatus() != null) {
            c.setProfileStatus(req.getProfileStatus());
        }

        Candidate updated = candidateRepository.save(c);
        logService.log(currentUser, "CANDIDATE", "UPDATE", updated.getCandidateCode(), "Updated profile for " + updated.getFullName());
        return updated;
    }

    public Candidate getCandidate(int id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new CandidateNotFoundException("Candidate ID " + id + " not found"));
    }

    public Page<Candidate> getCandidates(String search, String technology, String profileStatus, String placementStatus, Pageable pageable) {
        return candidateRepository.filterCandidates(search, technology, profileStatus, placementStatus, pageable);
    }

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public List<Candidate> searchByTechnology(String technology) {
        return candidateRepository.findByPrimaryTechnologyIgnoreCase(technology);
    }

    @Transactional
    public void deactivate(int id, User currentUser) {
        Candidate c = getCandidate(id);
        c.setProfileStatus("INACTIVE");
        candidateRepository.save(c);
        logService.log(currentUser, "CANDIDATE", "DEACTIVATE", c.getCandidateCode(), "Deactivated candidate: " + c.getFullName());
    }

    @Transactional
    public void deleteCandidate(int id, User currentUser) {
        Candidate c = getCandidate(id);
        // Safeguard: cannot delete candidate if they have applications
        if (!applicationRepository.findByCandidateCandidateId(id).isEmpty()) {
            throw new BadRequestException("Cannot delete candidate because applications exist. Deactivate instead.");
        }
        candidateRepository.delete(c);
        logService.log(currentUser, "CANDIDATE", "DELETE", c.getCandidateCode(), "Deleted candidate: " + c.getFullName());
    }
}
