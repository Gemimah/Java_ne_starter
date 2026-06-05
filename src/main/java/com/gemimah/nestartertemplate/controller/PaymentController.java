package com.gemimah.nestartertemplate.controller;

import com.gemimah.nestartertemplate.dto.PaymentRequest;
import com.gemimah.nestartertemplate.dto.PaymentResponse;
import com.gemimah.nestartertemplate.service.PaymentService;
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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping
	@PreAuthorize("hasRole('FINANCE')")
	public ResponseEntity<PaymentResponse> record(@Valid @RequestBody PaymentRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.record(request));
	}

	@GetMapping("/bill/{billId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
	public ResponseEntity<List<PaymentResponse>> getByBill(@PathVariable Long billId) {
		return ResponseEntity.ok(paymentService.getByBill(billId));
	}
}
