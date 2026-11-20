package com.kiranacademy.rpms.controller;

import com.kiranacademy.rpms.dto.common.ApiResponse;
import com.kiranacademy.rpms.dto.common.PagedResponse;
import com.kiranacademy.rpms.dto.placement.PlacementRequest;
import com.kiranacademy.rpms.dto.placement.PlacementStatusUpdateRequest;
import com.kiranacademy.rpms.entity.Placement;
import com.kiranacademy.rpms.service.PlacementService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/placements")
public class PlacementController extends BaseController {

    private final PlacementService placementService;

    public PlacementController(PlacementService placementService) {
        this.placementService = placementService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Placement>>> getPlacements(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String joiningStatus,
            @RequestParam(required = false) String placementStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Placement> result = placementService.getPlacements(
                search, joiningStatus, placementStatus,
                PageRequest.of(page, size, Sort.by("placementId").descending())
        );
        return ResponseEntity.ok(ApiResponse.ok(new PagedResponse<>(result)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Placement>> getPlacementById(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.ok(placementService.getPlacement(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Placement>> createPlacement(@Valid @RequestBody PlacementRequest request) {
        Placement created = placementService.recordPlacement(request, getCurrentUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Placement recorded successfully", created));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Placement>> updateStatus(
            @PathVariable int id,
            @Valid @RequestBody PlacementStatusUpdateRequest request
    ) {
        Placement updated = placementService.updateJoiningStatus(id, request, getCurrentUser());
        return ResponseEntity.ok(ApiResponse.ok("Placement joining status updated", updated));
    }
}
