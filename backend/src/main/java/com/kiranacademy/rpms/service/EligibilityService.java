package com.kiranacademy.rpms.service;

import com.kiranacademy.rpms.entity.Candidate;
import com.kiranacademy.rpms.entity.Job;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Preserves the exact business rules from the original EligibilityService.java
 */
@Service
public class EligibilityService {

    public List<String> getFailureReasons(Candidate c, Job j) {
        List<String> reasons = new ArrayList<>();

        if (!"ACTIVE".equalsIgnoreCase(c.getProfileStatus())) {
            reasons.add("Candidate profile is not ACTIVE");
        }

        if (!"OPEN".equalsIgnoreCase(j.getStatus())) {
            reasons.add("Job is not OPEN");
        }

        if (j.getApplicationDeadline() != null &&
                LocalDate.now().isAfter(j.getApplicationDeadline())) {
            reasons.add("Application deadline has passed");
        }

        if (c.getPercentage() != null && j.getMinPercentage() != null) {
            if (c.getPercentage().compareTo(j.getMinPercentage()) < 0) {
                reasons.add("Percentage below minimum requirement (" + j.getMinPercentage() + "%)");
            }
        }

        if (j.getMinPassingYear() != null && j.getMinPassingYear() > 0 &&
                c.getPassingYear() < j.getMinPassingYear()) {
            reasons.add("Passing year is below allowed minimum (" + j.getMinPassingYear() + ")");
        }

        if (j.getMaxPassingYear() != null && j.getMaxPassingYear() > 0 &&
                c.getPassingYear() > j.getMaxPassingYear()) {
            reasons.add("Passing year is above allowed maximum (" + j.getMaxPassingYear() + ")");
        }

        if (j.getMaxBacklogs() != null && c.getBacklogCount() > j.getMaxBacklogs()) {
            reasons.add("Backlog count (" + c.getBacklogCount() + ") exceeds allowed limit (" + j.getMaxBacklogs() + ")");
        }

        return reasons;
    }

    public boolean isEligible(Candidate c, Job j) {
        return getFailureReasons(c, j).isEmpty();
    }
}
