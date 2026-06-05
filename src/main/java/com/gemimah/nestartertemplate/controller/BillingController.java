package com.gemimah.nestartertemplate.controller;

import com.gemimah.nestartertemplate.dto.BillResponse;
import com.gemimah.nestartertemplate.dto.GenerateBillRequest;
import com.gemimah.nestartertemplate.service.BillingService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillingController {

	private final BillingService billingService;

	@PostMapping("/generate")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
	public ResponseEntity<BillResponse> generate(@Valid @RequestBody GenerateBillRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(billingService.generate(request));
	}

	@GetMapping("/customer/{customerId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
	public ResponseEntity<List<BillResponse>> byCustomer(@PathVariable Long customerId) {
		return ResponseEntity.ok(billingService.getCustomerBills(customerId));
	}
}
