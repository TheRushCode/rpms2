package com.kiranacademy.rpms.controller;

import com.kiranacademy.rpms.dto.common.ApiResponse;
import com.kiranacademy.rpms.dto.common.PagedResponse;
import com.kiranacademy.rpms.dto.company.CompanyRequest;
import com.kiranacademy.rpms.entity.Company;
import com.kiranacademy.rpms.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController extends BaseController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Company>>> getCompanies(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "companyName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = "desc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Page<Company> result = companyService.getCompanies(search, status, PageRequest.of(page, size, sort));
        return ResponseEntity.ok(ApiResponse.ok(new PagedResponse<>(result)));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Company>>> getActiveCompanies() {
        return ResponseEntity.ok(ApiResponse.ok(companyService.getActiveCompanies()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Company>> getCompanyById(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.ok(companyService.getCompany(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Company>> createCompany(@Valid @RequestBody CompanyRequest request) {
        Company saved = companyService.createCompany(request, getCurrentUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Company added successfully", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Company>> updateCompany(@PathVariable int id, @Valid @RequestBody CompanyRequest request) {
        Company updated = companyService.updateCompany(id, request, getCurrentUser());
        return ResponseEntity.ok(ApiResponse.ok("Company updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(@PathVariable int id) {
        companyService.deleteCompany(id, getCurrentUser());
        return ResponseEntity.ok(ApiResponse.ok("Company removed successfully", null));
    }
}
