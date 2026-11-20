package com.kiranacademy.rpms.repository;

import com.kiranacademy.rpms.entity.InterviewRound;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRoundRepository extends JpaRepository<InterviewRound, Integer> {
    List<InterviewRound> findByApplicationApplicationIdOrderByRoundNumberAsc(Integer applicationId);
    Optional<InterviewRound> findByApplicationApplicationIdAndRoundNumber(Integer applicationId, Integer roundNumber);

    @Query("SELECT i FROM InterviewRound i WHERE " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:mode IS NULL OR i.mode = :mode)")
    Page<InterviewRound> filterInterviews(
            @Param("status") String status,
            @Param("mode") String mode,
            Pageable pageable
    );
}
