package com.kiranacademy.rpms.service;

import com.kiranacademy.rpms.dto.interview.InterviewFeedbackRequest;
import com.kiranacademy.rpms.dto.interview.InterviewRequest;
import com.kiranacademy.rpms.entity.Application;
import com.kiranacademy.rpms.entity.InterviewFeedback;
import com.kiranacademy.rpms.entity.InterviewRound;
import com.kiranacademy.rpms.entity.User;
import com.kiranacademy.rpms.exception.BadRequestException;
import com.kiranacademy.rpms.exception.ResourceNotFoundException;
import com.kiranacademy.rpms.repository.InterviewFeedbackRepository;
import com.kiranacademy.rpms.repository.InterviewRoundRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InterviewService {

    private final InterviewRoundRepository roundRepository;
    private final InterviewFeedbackRepository feedbackRepository;
    private final ApplicationService applicationService;
    private final ActivityLogService logService;

    public InterviewService(InterviewRoundRepository roundRepository,
                            InterviewFeedbackRepository feedbackRepository,
                            ApplicationService applicationService,
                            ActivityLogService logService) {
        this.roundRepository = roundRepository;
        this.feedbackRepository = feedbackRepository;
        this.applicationService = applicationService;
        this.logService = logService;
    }

    @Transactional
    public InterviewRound scheduleInterview(InterviewRequest req, User currentUser) {
        Application app = applicationService.getApplication(req.getApplicationId());

        InterviewRound round = new InterviewRound();
        round.setApplication(app);
        round.setRoundNumber(req.getRoundNumber());
        round.setRoundType(req.getRoundType());
        round.setScheduledDate(req.getScheduledDate());
        round.setScheduledTime(req.getScheduledTime());
        round.setMode(req.getMode());
        round.setInterviewerName(req.getInterviewerName());
        round.setMeetingOrLocation(req.getMeetingOrLocation());
        round.setStatus(req.getStatus() != null ? req.getStatus() : "SCHEDULED");

        InterviewRound saved = roundRepository.save(round);

        // Update application status
        applicationService.updateStatus(app.getApplicationId(), "INTERVIEW_SCHEDULED",
                "Scheduled Round " + req.getRoundNumber() + " (" + req.getRoundType() + ")", currentUser);

        logService.log(currentUser, "INTERVIEW", "SCHEDULE", String.valueOf(saved.getInterviewId()),
                "Scheduled interview round " + req.getRoundNumber() + " for application " + app.getApplicationCode());

        return saved;
    }

    @Transactional
    public InterviewFeedback submitFeedback(int interviewId, InterviewFeedbackRequest req, User currentUser) {
        InterviewRound round = roundRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview round ID " + interviewId + " not found"));

        InterviewFeedback feedback = feedbackRepository.findByInterviewRoundInterviewId(interviewId)
                .orElse(new InterviewFeedback());

        feedback.setInterviewRound(round);
        feedback.setTechnicalRating(req.getTechnicalRating());
        feedback.setCommunicationRating(req.getCommunicationRating());
        feedback.setProblemSolvingRating(req.getProblemSolvingRating());
        feedback.setOverallRating(req.getOverallRating());
        feedback.setRecommendation(req.getRecommendation());
        feedback.setRemarks(req.getRemarks());
        feedback.setSubmittedBy(currentUser);

        InterviewFeedback saved = feedbackRepository.save(feedback);

        round.setStatus("COMPLETED");
        roundRepository.save(round);

        // Transition application workflow status based on recommendation
        if ("SELECTED".equalsIgnoreCase(req.getRecommendation())) {
            applicationService.updateStatus(round.getApplication().getApplicationId(), "SELECTED",
                    "Selected after Round " + round.getRoundNumber(), currentUser);
        } else if ("REJECTED".equalsIgnoreCase(req.getRecommendation())) {
            applicationService.updateStatus(round.getApplication().getApplicationId(), "REJECTED",
                    "Rejected after Round " + round.getRoundNumber(), currentUser);
        } else {
            applicationService.updateStatus(round.getApplication().getApplicationId(), "INTERVIEWED",
                    "Completed Round " + round.getRoundNumber() + " - Recommendation: " + req.getRecommendation(), currentUser);
        }

        logService.log(currentUser, "INTERVIEW", "FEEDBACK", String.valueOf(round.getInterviewId()),
                "Feedback submitted for Round " + round.getRoundNumber() + " (" + req.getRecommendation() + ")");

        return saved;
    }

    public Page<InterviewRound> getInterviews(String status, String mode, Pageable pageable) {
        return roundRepository.filterInterviews(status, mode, pageable);
    }

    public List<InterviewRound> getInterviewsForApplication(int applicationId) {
        return roundRepository.findByApplicationApplicationIdOrderByRoundNumberAsc(applicationId);
    }

    public InterviewRound getInterview(int id) {
        return roundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview ID " + id + " not found"));
    }
}
