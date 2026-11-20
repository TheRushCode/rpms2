package com.kiranacademy.rpms.controller;

import com.kiranacademy.rpms.dto.common.ApiResponse;
import com.kiranacademy.rpms.dto.common.PagedResponse;
import com.kiranacademy.rpms.dto.interview.InterviewFeedbackRequest;
import com.kiranacademy.rpms.dto.interview.InterviewRequest;
import com.kiranacademy.rpms.entity.InterviewFeedback;
import com.kiranacademy.rpms.entity.InterviewRound;
import com.kiranacademy.rpms.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController extends BaseController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<InterviewRound>>> getInterviews(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String mode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<InterviewRound> result = interviewService.getInterviews(
                status, mode, PageRequest.of(page, size, Sort.by("scheduledDate").descending())
        );
        return ResponseEntity.ok(ApiResponse.ok(new PagedResponse<>(result)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InterviewRound>> getInterviewById(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.ok(interviewService.getInterview(id)));
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<ApiResponse<List<InterviewRound>>> getInterviewsByApplication(@PathVariable int applicationId) {
        return ResponseEntity.ok(ApiResponse.ok(interviewService.getInterviewsForApplication(applicationId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InterviewRound>> scheduleInterview(@Valid @RequestBody InterviewRequest request) {
        InterviewRound round = interviewService.scheduleInterview(request, getCurrentUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Interview round scheduled", round));
    }

    @PostMapping("/{id}/feedback")
    public ResponseEntity<ApiResponse<InterviewFeedback>> submitFeedback(
            @PathVariable int id,
            @Valid @RequestBody InterviewFeedbackRequest request
    ) {
        InterviewFeedback feedback = interviewService.submitFeedback(id, request, getCurrentUser());
        return ResponseEntity.ok(ApiResponse.ok("Feedback submitted successfully", feedback));
    }
}
