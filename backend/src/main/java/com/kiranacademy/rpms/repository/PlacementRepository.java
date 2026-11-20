package com.kiranacademy.rpms.repository;

import com.kiranacademy.rpms.entity.Placement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlacementRepository extends JpaRepository<Placement, Integer> {
    Optional<Placement> findByOfferOfferId(Integer offerId);

    @Query("SELECT p FROM Placement p WHERE " +
           "(:search IS NULL OR LOWER(p.candidate.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.company.companyName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.job.jobTitle) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:joiningStatus IS NULL OR p.joiningStatus = :joiningStatus) AND " +
           "(:placementStatus IS NULL OR p.placementStatus = :placementStatus)")
    Page<Placement> filterPlacements(
            @Param("search") String search,
            @Param("joiningStatus") String joiningStatus,
            @Param("placementStatus") String placementStatus,
            Pageable pageable
    );

    @Query("SELECT p.company.companyName, COUNT(p) FROM Placement p WHERE p.joiningStatus = 'JOINED' GROUP BY p.company.companyName ORDER BY COUNT(p) DESC")
    List<Object[]> countPlacementsByCompany();

    long countByJoiningStatus(String joiningStatus);
}
