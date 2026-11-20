package com.kiranacademy.rpms.service;

import com.kiranacademy.rpms.dto.offer.OfferRequest;
import com.kiranacademy.rpms.entity.Application;
import com.kiranacademy.rpms.entity.Offer;
import com.kiranacademy.rpms.entity.User;
import com.kiranacademy.rpms.exception.BadRequestException;
import com.kiranacademy.rpms.exception.ResourceNotFoundException;
import com.kiranacademy.rpms.repository.OfferRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class OfferService {

    private final OfferRepository offerRepository;
    private final ApplicationService applicationService;
    private final ActivityLogService logService;

    public OfferService(OfferRepository offerRepository,
                        ApplicationService applicationService,
                        ActivityLogService logService) {
        this.offerRepository = offerRepository;
        this.applicationService = applicationService;
        this.logService = logService;
    }

    @Transactional
    public Offer createOffer(OfferRequest req, User currentUser) {
        Application app = applicationService.getApplication(req.getApplicationId());

        if (offerRepository.findByApplicationApplicationId(app.getApplicationId()).isPresent()) {
            throw new BadRequestException("Offer already exists for this application");
        }

        String offerCode = "OFF-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Offer offer = new Offer();
        offer.setOfferCode(offerCode);
        offer.setApplication(app);
        offer.setDesignation(req.getDesignation());
        offer.setOfferedPackageLpa(req.getOfferedPackageLpa());
        offer.setOfferDate(req.getOfferDate());
        offer.setExpectedJoiningDate(req.getExpectedJoiningDate());
        offer.setOfferStatus(req.getOfferStatus() != null ? req.getOfferStatus() : "OFFERED");
        offer.setRemarks(req.getRemarks());

        Offer saved = offerRepository.save(offer);

        applicationService.updateStatus(app.getApplicationId(), "OFFERED",
                "Offer created (" + saved.getDesignation() + " @ " + saved.getOfferedPackageLpa() + " LPA)", currentUser);

        logService.log(currentUser, "OFFER", "CREATE", saved.getOfferCode(),
                "Generated offer for application: " + app.getApplicationCode());

        return saved;
    }

    @Transactional
    public Offer updateStatus(int offerId, String status, User currentUser) {
        Offer offer = getOffer(offerId);
        offer.setOfferStatus(status);
        Offer updated = offerRepository.save(offer);

        logService.log(currentUser, "OFFER", "STATUS_CHANGE", updated.getOfferCode(),
                "Offer status updated to " + status);

        return updated;
    }

    public Offer getOffer(int id) {
        return offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer ID " + id + " not found"));
    }

    public Page<Offer> getOffers(String status, Pageable pageable) {
        return offerRepository.filterOffers(status, pageable);
    }
}
