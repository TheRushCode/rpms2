package com.kiranacademy.rpms.controller;

import com.kiranacademy.rpms.dto.common.ApiResponse;
import com.kiranacademy.rpms.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Preserves menu item 5: Candidate Technology Report
     */
    @GetMapping("/candidate-technology")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getCandidateTechReport() {
        return ResponseEntity.ok(ApiResponse.ok(reportService.candidateCountByTechnology()));
    }

    @GetMapping("/applications-by-status")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getApplicationsByStatus() {
        return ResponseEntity.ok(ApiResponse.ok(reportService.applicationsByStatus()));
    }

    @GetMapping("/placements-by-company")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPlacementsByCompany() {
        return ResponseEntity.ok(ApiResponse.ok(reportService.placementsByCompany()));
    }

    @GetMapping(value = "/candidates/csv", produces = "text/csv")
    public ResponseEntity<String> exportCandidatesCsv() {
        String csv = reportService.generateCandidateCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=candidates.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping(value = "/placements/csv", produces = "text/csv")
    public ResponseEntity<String> exportPlacementsCsv() {
        String csv = reportService.generatePlacementCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=placements.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping(value = "/jobs/csv", produces = "text/csv")
    public ResponseEntity<String> exportJobsCsv() {
        String csv = reportService.generateJobCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=jobs.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
