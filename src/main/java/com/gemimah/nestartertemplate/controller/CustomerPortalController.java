package com.gemimah.nestartertemplate.controller;

import com.gemimah.nestartertemplate.dto.BillResponse;
import com.gemimah.nestartertemplate.dto.PaymentResponse;
import com.gemimah.nestartertemplate.entity.Customer;
import com.gemimah.nestartertemplate.exception.ResourceNotFoundException;
import com.gemimah.nestartertemplate.repository.CustomerRepository;
import com.gemimah.nestartertemplate.service.BillingService;
import com.gemimah.nestartertemplate.service.PaymentService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerPortalController {

	private final CustomerRepository customerRepository;
	private final BillingService billingService;
	private final PaymentService paymentService;

	@GetMapping("/bills")
	@PreAuthorize("hasRole('CUSTOMER')")
	public List<BillResponse> myBills(Authentication authentication) {
		Customer customer = customerRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for current user"));
		return billingService.getCustomerBills(customer.getId());
	}

	@GetMapping("/payments")
	@PreAuthorize("hasRole('CUSTOMER')")
	public List<PaymentResponse> myPayments(Authentication authentication) {
		Customer customer = customerRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for current user"));
		List<PaymentResponse> all = new ArrayList<>();
		for (BillResponse bill : billingService.getCustomerBills(customer.getId())) {
			all.addAll(paymentService.getByBill(bill.id()));
		}
		return all;
	}
}
