package com.kiranacademy.rpms.controller;

import com.kiranacademy.rpms.dto.application.ApplicationRequest;
import com.kiranacademy.rpms.dto.application.ApplicationStatusUpdateRequest;
import com.kiranacademy.rpms.dto.common.ApiResponse;
import com.kiranacademy.rpms.dto.common.PagedResponse;
import com.kiranacademy.rpms.entity.Application;
import com.kiranacademy.rpms.entity.ApplicationHistory;
import com.kiranacademy.rpms.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController extends BaseController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Application>>> getApplications(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String eligibility,
            @RequestParam(required = false) Integer jobId,
            @RequestParam(required = false) Integer candidateId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "applicationId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<Application> result = applicationService.getApplications(
                search, status, eligibility, jobId, candidateId, PageRequest.of(page, size, sort)
        );
        return ResponseEntity.ok(ApiResponse.ok(new PagedResponse<>(result)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Application>> getApplicationById(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getApplication(id)));
    }

    /**
     * Preserves menu item 4: Apply for Job (with duplicate & eligibility check)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Application>> applyForJob(@Valid @RequestBody ApplicationRequest request) {
        Application saved = applicationService.applyForJob(request, getCurrentUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Application submitted successfully", saved));
    }

    @GetMapping("/check-eligibility")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkEligibility(
            @RequestParam int candidateId,
            @RequestParam int jobId
    ) {
        Map<String, Object> result = applicationService.checkEligibility(candidateId, jobId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Application>> updateStatus(
            @PathVariable int id,
            @Valid @RequestBody ApplicationStatusUpdateRequest request
    ) {
        Application updated = applicationService.updateStatus(id, request.getNewStatus(), request.getRemarks(), getCurrentUser());
        return ResponseEntity.ok(ApiResponse.ok("Status updated successfully", updated));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<List<ApplicationHistory>>> getHistory(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getApplicationHistory(id)));
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<ApiResponse<List<Application>>> getCandidateApplications(@PathVariable int candidateId) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getCandidateApplications(candidateId)));
    }
}
