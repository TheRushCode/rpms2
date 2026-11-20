package com.kiranacademy.rpms.repository;

import com.kiranacademy.rpms.entity.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Integer> {
    Optional<Candidate> findByCandidateCode(String candidateCode);
    Optional<Candidate> findByEmail(String email);
    Optional<Candidate> findByMobile(String mobile);
    boolean existsByCandidateCode(String candidateCode);
    boolean existsByEmail(String email);
    boolean existsByMobile(String mobile);

    List<Candidate> findByPrimaryTechnologyIgnoreCase(String technology);

    @Query("SELECT c FROM Candidate c WHERE " +
           "(:search IS NULL OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.candidateCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.mobile) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:technology IS NULL OR LOWER(c.primaryTechnology) = LOWER(:technology)) AND " +
           "(:profileStatus IS NULL OR c.profileStatus = :profileStatus) AND " +
           "(:placementStatus IS NULL OR c.placementStatus = :placementStatus)")
    Page<Candidate> filterCandidates(
            @Param("search") String search,
            @Param("technology") String technology,
            @Param("profileStatus") String profileStatus,
            @Param("placementStatus") String placementStatus,
            Pageable pageable
    );

    @Query("SELECT c.primaryTechnology, COUNT(c) FROM Candidate c GROUP BY c.primaryTechnology ORDER BY COUNT(c) DESC")
    List<Object[]> countCandidatesByTechnology();

    long countByPlacementStatus(String placementStatus);
}
