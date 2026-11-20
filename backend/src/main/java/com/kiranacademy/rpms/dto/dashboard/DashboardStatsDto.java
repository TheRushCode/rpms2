package com.kiranacademy.rpms.dto.dashboard;

import java.util.List;
import java.util.Map;

public class DashboardStatsDto {
    private long totalCandidates;
    private long placedCandidates;
    private long totalJobs;
    private long openJobs;
    private long totalApplications;
    private long totalCompanies;
    private long activePlacements;

    private List<Map<String, Object>> candidateTechnologyDistribution;
    private List<Map<String, Object>> applicationsByStatus;
    private List<Map<String, Object>> placementsByCompany;
    private List<Map<String, Object>> recentActivities;

    public DashboardStatsDto() {}

    public long getTotalCandidates() { return totalCandidates; }
    public void setTotalCandidates(long totalCandidates) { this.totalCandidates = totalCandidates; }
    public long getPlacedCandidates() { return placedCandidates; }
    public void setPlacedCandidates(long placedCandidates) { this.placedCandidates = placedCandidates; }
    public long getTotalJobs() { return totalJobs; }
    public void setTotalJobs(long totalJobs) { this.totalJobs = totalJobs; }
    public long getOpenJobs() { return openJobs; }
    public void setOpenJobs(long openJobs) { this.openJobs = openJobs; }
    public long getTotalApplications() { return totalApplications; }
    public void setTotalApplications(long totalApplications) { this.totalApplications = totalApplications; }
    public long getTotalCompanies() { return totalCompanies; }
    public void setTotalCompanies(long totalCompanies) { this.totalCompanies = totalCompanies; }
    public long getActivePlacements() { return activePlacements; }
    public void setActivePlacements(long activePlacements) { this.activePlacements = activePlacements; }
    public List<Map<String, Object>> getCandidateTechnologyDistribution() { return candidateTechnologyDistribution; }
    public void setCandidateTechnologyDistribution(List<Map<String, Object>> list) { this.candidateTechnologyDistribution = list; }
    public List<Map<String, Object>> getApplicationsByStatus() { return applicationsByStatus; }
    public void setApplicationsByStatus(List<Map<String, Object>> list) { this.applicationsByStatus = list; }
    public List<Map<String, Object>> getPlacementsByCompany() { return placementsByCompany; }
    public void setPlacementsByCompany(List<Map<String, Object>> list) { this.placementsByCompany = list; }
    public List<Map<String, Object>> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(List<Map<String, Object>> list) { this.recentActivities = list; }
}
