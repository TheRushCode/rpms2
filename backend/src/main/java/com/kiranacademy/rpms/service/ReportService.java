package com.kiranacademy.rpms.service;

import com.kiranacademy.rpms.entity.Candidate;
import com.kiranacademy.rpms.entity.Job;
import com.kiranacademy.rpms.entity.Placement;
import com.kiranacademy.rpms.repository.ApplicationRepository;
import com.kiranacademy.rpms.repository.CandidateRepository;
import com.kiranacademy.rpms.repository.JobRepository;
import com.kiranacademy.rpms.repository.PlacementRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReportService {

    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final PlacementRepository placementRepository;

    public ReportService(CandidateRepository candidateRepository,
                         JobRepository jobRepository,
                         ApplicationRepository applicationRepository,
                         PlacementRepository placementRepository) {
        this.candidateRepository = candidateRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.placementRepository = placementRepository;
    }

    /**
     * Preserves original ReportService candidateCountByTechnology()
     */
    public Map<String, Integer> candidateCountByTechnology() {
        Map<String, Integer> map = new LinkedHashMap<>();
        List<Object[]> rows = candidateRepository.countCandidatesByTechnology();
        for (Object[] row : rows) {
            String tech = row[0] != null ? row[0].toString() : "Not Specified";
            Number count = (Number) row[1];
            map.put(tech, count.intValue());
        }
        return map;
    }

    public List<Map<String, Object>> applicationsByStatus() {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Object[]> rows = applicationRepository.countApplicationsByStatus();
        for (Object[] r : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("status", r[0]);
            item.put("count", r[1]);
            list.add(item);
        }
        return list;
    }

    public List<Map<String, Object>> placementsByCompany() {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Object[]> rows = placementRepository.countPlacementsByCompany();
        for (Object[] r : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("companyName", r[0]);
            item.put("placedCount", r[1]);
            list.add(item);
        }
        return list;
    }

    public String generateCandidateCsv() {
        List<Candidate> list = candidateRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Candidate Code,Full Name,Email,Mobile,Qualification,Passing Year,Percentage,Backlogs,Primary Tech,Profile Status,Placement Status\n");
        for (Candidate c : list) {
            sb.append(c.getCandidateId()).append(",")
              .append(escape(c.getCandidateCode())).append(",")
              .append(escape(c.getFullName())).append(",")
              .append(escape(c.getEmail())).append(",")
              .append(escape(c.getMobile())).append(",")
              .append(escape(c.getQualification())).append(",")
              .append(c.getPassingYear()).append(",")
              .append(c.getPercentage()).append(",")
              .append(c.getBacklogCount()).append(",")
              .append(escape(c.getPrimaryTechnology())).append(",")
              .append(c.getProfileStatus()).append(",")
              .append(c.getPlacementStatus()).append("\n");
        }
        return sb.toString();
    }

    public String generatePlacementCsv() {
        List<Placement> list = placementRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("Placement ID,Candidate Code,Candidate Name,Company,Job Title,Package (LPA),Joining Status,Placement Status\n");
        for (Placement p : list) {
            sb.append(p.getPlacementId()).append(",")
              .append(escape(p.getCandidate().getCandidateCode())).append(",")
              .append(escape(p.getCandidate().getFullName())).append(",")
              .append(escape(p.getCompany().getCompanyName())).append(",")
              .append(escape(p.getJob().getJobTitle())).append(",")
              .append(p.getFinalPackageLpa()).append(",")
              .append(p.getJoiningStatus()).append(",")
              .append(p.getPlacementStatus()).append("\n");
        }
        return sb.toString();
    }

    public String generateJobCsv() {
        List<Job> list = jobRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("Job ID,Job Code,Company,Title,Location,Openings,Min LPA,Max LPA,Min %,Min Year,Max Year,Max Backlogs,Deadline,Status\n");
        for (Job j : list) {
            sb.append(j.getJobId()).append(",")
              .append(escape(j.getJobCode())).append(",")
              .append(escape(j.getCompany().getCompanyName())).append(",")
              .append(escape(j.getJobTitle())).append(",")
              .append(escape(j.getLocation())).append(",")
              .append(j.getOpenings()).append(",")
              .append(j.getMinPackageLpa()).append(",")
              .append(j.getMaxPackageLpa()).append(",")
              .append(j.getMinPercentage()).append(",")
              .append(j.getMinPassingYear()).append(",")
              .append(j.getMaxPassingYear()).append(",")
              .append(j.getMaxBacklogs()).append(",")
              .append(j.getApplicationDeadline()).append(",")
              .append(j.getStatus()).append("\n");
        }
        return sb.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return "\"" + s.replace("\"", "\\\"") + "\"";
    }
}
