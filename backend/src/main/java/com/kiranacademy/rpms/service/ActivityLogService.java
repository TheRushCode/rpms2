package com.kiranacademy.rpms.service;

import com.kiranacademy.rpms.entity.ActivityLog;
import com.kiranacademy.rpms.entity.User;
import com.kiranacademy.rpms.repository.ActivityLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Transactional
    public void log(User user, String module, String action, String recordId, String description) {
        ActivityLog log = new ActivityLog(user, module, action, recordId, description);
        activityLogRepository.save(log);
    }

    public List<ActivityLog> getRecentLogs() {
        return activityLogRepository.findTop20ByOrderByLoggedAtDesc();
    }

    public Page<ActivityLog> getLogs(String module, String action, Pageable pageable) {
        return activityLogRepository.filterLogs(module, action, pageable);
    }
}
