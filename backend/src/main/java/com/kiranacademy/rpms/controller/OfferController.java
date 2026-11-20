package com.kiranacademy.rpms.controller;

import com.kiranacademy.rpms.dto.common.ApiResponse;
import com.kiranacademy.rpms.dto.common.PagedResponse;
import com.kiranacademy.rpms.dto.offer.OfferRequest;
import com.kiranacademy.rpms.entity.Offer;
import com.kiranacademy.rpms.service.OfferService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/offers")
public class OfferController extends BaseController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Offer>>> getOffers(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Offer> result = offerService.getOffers(status, PageRequest.of(page, size, Sort.by("offerId").descending()));
        return ResponseEntity.ok(ApiResponse.ok(new PagedResponse<>(result)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Offer>> getOfferById(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.ok(offerService.getOffer(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Offer>> createOffer(@Valid @RequestBody OfferRequest request) {
        Offer created = offerService.createOffer(request, getCurrentUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Offer created successfully", created));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Offer>> updateStatus(@PathVariable int id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        Offer updated = offerService.updateStatus(id, status, getCurrentUser());
        return ResponseEntity.ok(ApiResponse.ok("Offer status updated to " + status, updated));
    }
}
