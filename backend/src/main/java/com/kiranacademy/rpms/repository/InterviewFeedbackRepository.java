package com.kiranacademy.rpms.repository;

import com.kiranacademy.rpms.entity.InterviewFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterviewFeedbackRepository extends JpaRepository<InterviewFeedback, Integer> {
    Optional<InterviewFeedback> findByInterviewRoundInterviewId(Integer interviewId);
}
