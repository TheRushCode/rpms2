package com.kiranacademy.rpms.service;

import com.kiranacademy.rpms.dto.placement.PlacementRequest;
import com.kiranacademy.rpms.dto.placement.PlacementStatusUpdateRequest;
import com.kiranacademy.rpms.entity.Candidate;
import com.kiranacademy.rpms.entity.Offer;
import com.kiranacademy.rpms.entity.Placement;
import com.kiranacademy.rpms.entity.User;
import com.kiranacademy.rpms.exception.BadRequestException;
import com.kiranacademy.rpms.exception.ResourceNotFoundException;
import com.kiranacademy.rpms.repository.CandidateRepository;
import com.kiranacademy.rpms.repository.OfferRepository;
import com.kiranacademy.rpms.repository.PlacementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class PlacementService {

    private final PlacementRepository placementRepository;
    private final OfferRepository offerRepository;
    private final CandidateRepository candidateRepository;
    private final ApplicationService applicationService;
    private final ActivityLogService logService;

    public PlacementService(PlacementRepository placementRepository,
                            OfferRepository offerRepository,
                            CandidateRepository candidateRepository,
                            ApplicationService applicationService,
                            ActivityLogService logService) {
        this.placementRepository = placementRepository;
        this.offerRepository = offerRepository;
        this.candidateRepository = candidateRepository;
        this.applicationService = applicationService;
        this.logService = logService;
    }

    @Transactional
    public Placement recordPlacement(PlacementRequest req, User currentUser) {
        Offer offer = offerRepository.findById(req.getOfferId())
                .orElseThrow(() -> new BadRequestException("Offer ID " + req.getOfferId() + " not found"));

        if (placementRepository.findByOfferOfferId(offer.getOfferId()).isPresent()) {
            throw new BadRequestException("Placement already recorded for this offer");
        }

        Candidate candidate = offer.getApplication().getCandidate();

        Placement placement = new Placement();
        placement.setCandidate(candidate);
        placement.setCompany(offer.getApplication().getJob().getCompany());
        placement.setJob(offer.getApplication().getJob());
        placement.setOffer(offer);
        placement.setDesignation(offer.getDesignation());
        placement.setFinalPackageLpa(offer.getOfferedPackageLpa());
        placement.setExpectedJoiningDate(req.getExpectedJoiningDate() != null ? req.getExpectedJoiningDate() : offer.getExpectedJoiningDate());
        placement.setActualJoiningDate(req.getActualJoiningDate());
        placement.setJoiningStatus(req.getJoiningStatus() != null ? req.getJoiningStatus() : "PENDING");
        placement.setPlacementStatus(req.getPlacementStatus() != null ? req.getPlacementStatus() : "ACTIVE");

        Placement saved = placementRepository.save(placement);

        // If marked as JOINED, update Candidate placementStatus to PLACED
        if ("JOINED".equalsIgnoreCase(saved.getJoiningStatus())) {
            candidate.setPlacementStatus("PLACED");
            candidateRepository.save(candidate);
            applicationService.updateStatus(offer.getApplication().getApplicationId(), "JOINED", "Candidate joined", currentUser);
        }

        logService.log(currentUser, "PLACEMENT", "CREATE", String.valueOf(saved.getPlacementId()),
                "Created placement for " + candidate.getFullName() + " at " + placement.getCompany().getCompanyName());

        return saved;
    }

    @Transactional
    public Placement updateJoiningStatus(int placementId, PlacementStatusUpdateRequest req, User currentUser) {
        Placement placement = getPlacement(placementId);
        String oldStatus = placement.getJoiningStatus();
        placement.setJoiningStatus(req.getJoiningStatus());
        if (req.getActualJoiningDate() != null) {
            placement.setActualJoiningDate(req.getActualJoiningDate());
        } else if ("JOINED".equalsIgnoreCase(req.getJoiningStatus()) && placement.getActualJoiningDate() == null) {
            placement.setActualJoiningDate(LocalDate.now());
        }

        Placement updated = placementRepository.save(placement);

        // Automatically update candidate placement status
        Candidate candidate = placement.getCandidate();
        if ("JOINED".equalsIgnoreCase(req.getJoiningStatus())) {
            candidate.setPlacementStatus("PLACED");
            candidateRepository.save(candidate);
            applicationService.updateStatus(placement.getOffer().getApplication().getApplicationId(),
                    "JOINED", "Candidate marked as joined", currentUser);
        } else if ("JOINED".equalsIgnoreCase(oldStatus) && !"JOINED".equalsIgnoreCase(req.getJoiningStatus())) {
            candidate.setPlacementStatus("NOT_PLACED");
            candidateRepository.save(candidate);
        }

        logService.log(currentUser, "PLACEMENT", "STATUS_CHANGE", String.valueOf(updated.getPlacementId()),
                "Placement joining status updated to " + req.getJoiningStatus());

        return updated;
    }

    public Placement getPlacement(int id) {
        return placementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placement ID " + id + " not found"));
    }

    public Page<Placement> getPlacements(String search, String joiningStatus, String placementStatus, Pageable pageable) {
        return placementRepository.filterPlacements(search, joiningStatus, placementStatus, pageable);
    }
}
