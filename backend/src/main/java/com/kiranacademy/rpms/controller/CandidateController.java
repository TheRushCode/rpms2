package com.kiranacademy.rpms.controller;

import com.kiranacademy.rpms.dto.candidate.CandidateRequest;
import com.kiranacademy.rpms.dto.common.ApiResponse;
import com.kiranacademy.rpms.dto.common.PagedResponse;
import com.kiranacademy.rpms.entity.Candidate;
import com.kiranacademy.rpms.service.CandidateService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController extends BaseController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Candidate>>> getCandidates(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String technology,
            @RequestParam(required = false) String profileStatus,
            @RequestParam(required = false) String placementStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "candidateId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = "desc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Page<Candidate> result = candidateService.getCandidates(
                search, technology, profileStatus, placementStatus, PageRequest.of(page, size, sort)
        );
        return ResponseEntity.ok(ApiResponse.ok(new PagedResponse<>(result)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Candidate>>> getAllCandidates() {
        return ResponseEntity.ok(ApiResponse.ok(candidateService.getAllCandidates()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Candidate>> getCandidateById(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.ok(candidateService.getCandidate(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Candidate>> createCandidate(@Valid @RequestBody CandidateRequest request) {
        Candidate saved = candidateService.addCandidate(request, getCurrentUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Candidate registered successfully", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Candidate>> updateCandidate(@PathVariable int id, @Valid @RequestBody CandidateRequest request) {
        Candidate updated = candidateService.updateCandidate(id, request, getCurrentUser());
        return ResponseEntity.ok(ApiResponse.ok("Candidate updated successfully", updated));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateCandidate(@PathVariable int id) {
        candidateService.deactivate(id, getCurrentUser());
        return ResponseEntity.ok(ApiResponse.ok("Candidate deactivated", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCandidate(@PathVariable int id) {
        candidateService.deleteCandidate(id, getCurrentUser());
        return ResponseEntity.ok(ApiResponse.ok("Candidate deleted successfully", null));
    }
}
