package com.gemimah.nestartertemplate.controller;

import com.gemimah.nestartertemplate.dto.BillResponse;
import com.gemimah.nestartertemplate.service.BillingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Tasks 7-9: bill generation, approval and viewing.
@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@Tag(name = "07. Bills", description = "Bill generation, approval and viewing")
public class BillingController {

	private final BillingService billingService;

	// Generate a bill from a captured meter reading.
	@PostMapping("/generate/{readingId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'OPERATOR')")
	public ResponseEntity<BillResponse> generate(@PathVariable Long readingId) {
		return ResponseEntity.status(HttpStatus.CREATED).body(billingService.generateFromReading(readingId));
	}

	// Approve a pending bill (notifies + emails the customer).
	@PutMapping("/{billId}/approve")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
	public ResponseEntity<BillResponse> approve(@PathVariable Long billId) {
		return ResponseEntity.ok(billingService.approve(billId));
	}

	@GetMapping("/customer/{customerId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'CUSTOMER')")
	public ResponseEntity<List<BillResponse>> byCustomer(@PathVariable Long customerId) {
		return ResponseEntity.ok(billingService.getCustomerBills(customerId));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'CUSTOMER')")
	public ResponseEntity<BillResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(billingService.getById(id));
	}
}
