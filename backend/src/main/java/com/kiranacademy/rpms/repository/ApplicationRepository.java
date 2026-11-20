package com.kiranacademy.rpms.repository;

import com.kiranacademy.rpms.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    Optional<Application> findByApplicationCode(String applicationCode);
    boolean existsByCandidateCandidateIdAndJobJobId(Integer candidateId, Integer jobId);

    List<Application> findByCandidateCandidateId(Integer candidateId);
    List<Application> findByJobJobId(Integer jobId);

    @Query("SELECT a FROM Application a WHERE " +
           "(:search IS NULL OR LOWER(a.applicationCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.candidate.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.job.jobTitle) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.job.company.companyName) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR a.applicationStatus = :status) AND " +
           "(:eligibility IS NULL OR a.eligibilityStatus = :eligibility) AND " +
           "(:jobId IS NULL OR a.job.jobId = :jobId) AND " +
           "(:candidateId IS NULL OR a.candidate.candidateId = :candidateId)")
    Page<Application> filterApplications(
            @Param("search") String search,
            @Param("status") String status,
            @Param("eligibility") String eligibility,
            @Param("jobId") Integer jobId,
            @Param("candidateId") Integer candidateId,
            Pageable pageable
    );

    @Query("SELECT a.applicationStatus, COUNT(a) FROM Application a GROUP BY a.applicationStatus")
    List<Object[]> countApplicationsByStatus();
}
