package com.kiranacademy.rpms.service;

import com.kiranacademy.rpms.dto.dashboard.DashboardStatsDto;
import com.kiranacademy.rpms.entity.ActivityLog;
import com.kiranacademy.rpms.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardService {

    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final CompanyRepository companyRepository;
    private final PlacementRepository placementRepository;
    private final ActivityLogRepository activityLogRepository;
    private final ReportService reportService;

    public DashboardService(CandidateRepository candidateRepository,
                            JobRepository jobRepository,
                            ApplicationRepository applicationRepository,
                            CompanyRepository companyRepository,
                            PlacementRepository placementRepository,
                            ActivityLogRepository activityLogRepository,
                            ReportService reportService) {
        this.candidateRepository = candidateRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.companyRepository = companyRepository;
        this.placementRepository = placementRepository;
        this.activityLogRepository = activityLogRepository;
        this.reportService = reportService;
    }

    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto dto = new DashboardStatsDto();
        dto.setTotalCandidates(candidateRepository.count());
        dto.setPlacedCandidates(candidateRepository.countByPlacementStatus("PLACED"));
        dto.setTotalJobs(jobRepository.count());
        dto.setOpenJobs(jobRepository.countByStatus("OPEN"));
        dto.setTotalApplications(applicationRepository.count());
        dto.setTotalCompanies(companyRepository.count());
        dto.setActivePlacements(placementRepository.countByJoiningStatus("JOINED"));

        // Technology distribution
        List<Map<String, Object>> techList = new ArrayList<>();
        reportService.candidateCountByTechnology().forEach((tech, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("technology", tech);
            item.put("count", count);
            techList.add(item);
        });
        dto.setCandidateTechnologyDistribution(techList);

        // Application status
        dto.setApplicationsByStatus(reportService.applicationsByStatus());

        // Placement by company
        dto.setPlacementsByCompany(reportService.placementsByCompany());

        // Recent activities
        List<Map<String, Object>> recentActivities = new ArrayList<>();
        for (ActivityLog log : activityLogRepository.findTop20ByOrderByLoggedAtDesc()) {
            Map<String, Object> item = new HashMap<>();
            item.put("logId", log.getLogId());
            item.put("module", log.getModuleName());
            item.put("action", log.getActionName());
            item.put("recordId", log.getRecordId());
            item.put("description", log.getDescription());
            item.put("loggedAt", log.getLoggedAt());
            recentActivities.add(item);
        }
        dto.setRecentActivities(recentActivities);

        return dto;
    }
}
