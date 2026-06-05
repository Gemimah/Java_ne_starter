package com.gemimah.nestartertemplate.controller;

import com.gemimah.nestartertemplate.dto.CustomerRequest;
import com.gemimah.nestartertemplate.dto.CustomerResponse;
import com.gemimah.nestartertemplate.dto.PageResponse;
import com.gemimah.nestartertemplate.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

	private final CustomerService customerService;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(request));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
	public ResponseEntity<PageResponse<CustomerResponse>> getAll(@PageableDefault(size = 20) Pageable pageable) {
		return ResponseEntity.ok(customerService.getAll(pageable));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
	public ResponseEntity<CustomerResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(customerService.getById(id));
	}
}
