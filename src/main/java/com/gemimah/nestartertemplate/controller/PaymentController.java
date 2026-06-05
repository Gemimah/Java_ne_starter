package com.gemimah.nestartertemplate.controller;

import com.gemimah.nestartertemplate.dto.PaymentRequest;
import com.gemimah.nestartertemplate.dto.PaymentResponse;
import com.gemimah.nestartertemplate.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
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

// Tasks 10 & 12: payment processing and history.
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "08. Payments", description = "Record payments and view history")
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping
	@PreAuthorize("hasAnyRole('FINANCE', 'CUSTOMER')")
	public ResponseEntity<PaymentResponse> record(@Valid @RequestBody PaymentRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.record(request));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
	public ResponseEntity<List<PaymentResponse>> getAll() {
		return ResponseEntity.ok(paymentService.getAll());
	}

	@GetMapping("/bill/{billId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'CUSTOMER')")
	public ResponseEntity<List<PaymentResponse>> getByBill(@PathVariable Long billId) {
		return ResponseEntity.ok(paymentService.getByBill(billId));
	}

	@GetMapping("/customer/{customerId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'CUSTOMER')")
	public ResponseEntity<List<PaymentResponse>> getByCustomer(@PathVariable Long customerId) {
		return ResponseEntity.ok(paymentService.getByCustomer(customerId));
	}
}
