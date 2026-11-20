package com.kiranacademy.rpms.controller;

import com.kiranacademy.rpms.dto.common.ApiResponse;
import com.kiranacademy.rpms.dto.common.PagedResponse;
import com.kiranacademy.rpms.dto.job.JobRequest;
import com.kiranacademy.rpms.entity.Job;
import com.kiranacademy.rpms.service.JobService;
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
@RequestMapping("/api/jobs")
public class JobController extends BaseController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Job>>> getJobs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "jobId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<Job> result = jobService.getJobs(search, status, companyId, PageRequest.of(page, size, sort));
        return ResponseEntity.ok(ApiResponse.ok(new PagedResponse<>(result)));
    }

    /**
     * Preserves menu item 3: View Open Jobs
     */
    @GetMapping("/open")
    public ResponseEntity<ApiResponse<List<Job>>> getOpenJobs() {
        return ResponseEntity.ok(ApiResponse.ok(jobService.getOpenJobs()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Job>> getJobById(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.getJob(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Job>> createJob(@Valid @RequestBody JobRequest request) {
        Job saved = jobService.createJob(request, getCurrentUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Job opening posted successfully", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Job>> updateJob(@PathVariable int id, @Valid @RequestBody JobRequest request) {
        Job updated = jobService.updateJob(id, request, getCurrentUser());
        return ResponseEntity.ok(ApiResponse.ok("Job opening updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateJobStatus(@PathVariable int id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        jobService.updateStatus(id, status, getCurrentUser());
        return ResponseEntity.ok(ApiResponse.ok("Job status updated", null));
    }
}
