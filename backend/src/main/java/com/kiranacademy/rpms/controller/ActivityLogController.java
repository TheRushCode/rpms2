package com.kiranacademy.rpms.controller;

import com.kiranacademy.rpms.dto.common.ApiResponse;
import com.kiranacademy.rpms.dto.common.PagedResponse;
import com.kiranacademy.rpms.entity.ActivityLog;
import com.kiranacademy.rpms.service.ActivityLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService logService;

    public ActivityLogController(ActivityLogService logService) {
        this.logService = logService;
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<ActivityLog>>> getRecentLogs() {
        return ResponseEntity.ok(ApiResponse.ok(logService.getRecentLogs()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ActivityLog>>> getLogs(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<ActivityLog> result = logService.getLogs(
                module, action, PageRequest.of(page, size, Sort.by("loggedAt").descending())
        );
        return ResponseEntity.ok(ApiResponse.ok(new PagedResponse<>(result)));
    }
}
