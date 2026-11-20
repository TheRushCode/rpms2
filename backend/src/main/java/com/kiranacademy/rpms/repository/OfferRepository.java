package com.kiranacademy.rpms.repository;

import com.kiranacademy.rpms.entity.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Integer> {
    Optional<Offer> findByOfferCode(String offerCode);
    Optional<Offer> findByApplicationApplicationId(Integer applicationId);
    boolean existsByOfferCode(String offerCode);

    @Query("SELECT o FROM Offer o WHERE " +
           "(:status IS NULL OR o.offerStatus = :status)")
    Page<Offer> filterOffers(@Param("status") String status, Pageable pageable);
}
