package com.kiranacademy.rpms.repository;

import com.kiranacademy.rpms.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {
    Optional<Job> findByJobCode(String jobCode);
    boolean existsByJobCode(String jobCode);
    List<Job> findByStatus(String status);
    List<Job> findByCompanyCompanyId(Integer companyId);

    @Query("SELECT j FROM Job j WHERE " +
           "(:search IS NULL OR LOWER(j.jobTitle) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(j.jobCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(j.company.companyName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(j.location) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR j.status = :status) AND " +
           "(:companyId IS NULL OR j.company.companyId = :companyId)")
    Page<Job> filterJobs(
            @Param("search") String search,
            @Param("status") String status,
            @Param("companyId") Integer companyId,
            Pageable pageable
    );

    long countByStatus(String status);
}
