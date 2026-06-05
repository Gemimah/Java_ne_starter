package com.gemimah.nestartertemplate.controller;

import com.gemimah.nestartertemplate.dto.BillResponse;
import com.gemimah.nestartertemplate.dto.NotificationResponse;
import com.gemimah.nestartertemplate.dto.PaymentResponse;
import com.gemimah.nestartertemplate.entity.Customer;
import com.gemimah.nestartertemplate.exception.ResourceNotFoundException;
import com.gemimah.nestartertemplate.repository.CustomerRepository;
import com.gemimah.nestartertemplate.service.BillingService;
import com.gemimah.nestartertemplate.service.NotificationService;
import com.gemimah.nestartertemplate.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Convenience self-service endpoints for the logged-in customer
// (resolves the customer by the JWT email, so no id is needed).
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Tag(name = "10. Customer Portal", description = "Logged-in customer self-service (no id needed)")
public class CustomerPortalController {

	private final CustomerRepository customerRepository;
	private final BillingService billingService;
	private final PaymentService paymentService;
	private final NotificationService notificationService;

	@GetMapping("/bills")
	@PreAuthorize("hasRole('CUSTOMER')")
	public List<BillResponse> myBills(Authentication authentication) {
		return billingService.getCustomerBills(currentCustomer(authentication).getId());
	}

	@GetMapping("/payments")
	@PreAuthorize("hasRole('CUSTOMER')")
	public List<PaymentResponse> myPayments(Authentication authentication) {
		return paymentService.getByCustomer(currentCustomer(authentication).getId());
	}

	@GetMapping("/notifications")
	@PreAuthorize("hasRole('CUSTOMER')")
	public List<NotificationResponse> myNotifications(Authentication authentication) {
		return notificationService.getByCustomer(currentCustomer(authentication).getId());
	}

	// Maps the authenticated user's email to a customer record.
	private Customer currentCustomer(Authentication authentication) {
		return customerRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for current user"));
	}
}
