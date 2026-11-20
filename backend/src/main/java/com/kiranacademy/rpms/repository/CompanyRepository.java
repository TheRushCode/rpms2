package com.kiranacademy.rpms.repository;

import com.kiranacademy.rpms.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Optional<Company> findByCompanyCode(String companyCode);
    boolean existsByCompanyCode(String companyCode);
    List<Company> findByStatus(String status);

    @Query("SELECT c FROM Company c WHERE " +
           "(:search IS NULL OR LOWER(c.companyName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.companyCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.industry) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.location) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR c.status = :status)")
    Page<Company> filterCompanies(
            @Param("search") String search,
            @Param("status") String status,
            Pageable pageable
    );
}
