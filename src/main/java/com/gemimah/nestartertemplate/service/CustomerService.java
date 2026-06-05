package com.gemimah.nestartertemplate.service;

import com.gemimah.nestartertemplate.dto.CustomerRequest;
import com.gemimah.nestartertemplate.dto.CustomerResponse;
import com.gemimah.nestartertemplate.dto.PageResponse;
import com.gemimah.nestartertemplate.entity.Customer;
import com.gemimah.nestartertemplate.entity.RecordStatus;
import com.gemimah.nestartertemplate.exception.ApiException;
import com.gemimah.nestartertemplate.exception.ResourceNotFoundException;
import com.gemimah.nestartertemplate.repository.BillRepository;
import com.gemimah.nestartertemplate.repository.CustomerRepository;
import com.gemimah.nestartertemplate.repository.MeterReadingRepository;
import com.gemimah.nestartertemplate.repository.MeterRepository;
import com.gemimah.nestartertemplate.repository.NotificationRepository;
import com.gemimah.nestartertemplate.repository.PaymentRepository;
import com.gemimah.nestartertemplate.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Customer CRUD. Enforces unique national ID and cascades deletes to related data.
@Service
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository customerRepository;
	private final MeterRepository meterRepository;
	private final MeterReadingRepository meterReadingRepository;
	private final BillRepository billRepository;
	private final PaymentRepository paymentRepository;
	private final NotificationRepository notificationRepository;

	// Create a customer; rejects duplicate national IDs.
	@Transactional
	public CustomerResponse create(CustomerRequest request) {
		if (customerRepository.existsByNationalId(request.nationalId())) {
			throw new ApiException("Customer with this national ID already exists", HttpStatus.CONFLICT);
		}
		Customer customer = Customer.builder()
				.fullNames(request.fullNames())
				.nationalId(request.nationalId())
				.email(request.email())
				.phoneNumber(request.phoneNumber())
				.address(request.address())
				.status(RecordStatus.valueOf(request.status().toUpperCase()))
				.build();
		return CustomerResponse.from(customerRepository.save(customer));
	}

	@Transactional(readOnly = true)
	public PageResponse<CustomerResponse> getAll(Pageable pageable) {
		return PaginationUtil.map(customerRepository.findAll(pageable), CustomerResponse::from);
	}

	@Transactional(readOnly = true)
	public Customer getEntity(Long id) {
		return customerRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
	}

	@Transactional(readOnly = true)
	public CustomerResponse getById(Long id) {
		return CustomerResponse.from(getEntity(id));
	}

	// Update editable fields. Allows changing national ID only if not taken by another customer.
	@Transactional
	public CustomerResponse update(Long id, CustomerRequest request) {
		Customer customer = getEntity(id);
		if (!customer.getNationalId().equals(request.nationalId())
				&& customerRepository.existsByNationalId(request.nationalId())) {
			throw new ApiException("Another customer already uses this national ID", HttpStatus.CONFLICT);
		}
		customer.setFullNames(request.fullNames());
		customer.setNationalId(request.nationalId());
		customer.setEmail(request.email());
		customer.setPhoneNumber(request.phoneNumber());
		customer.setAddress(request.address());

		RecordStatus newStatus = RecordStatus.valueOf(request.status().toUpperCase());
		customer.setStatus(newStatus);
		CustomerResponse response = CustomerResponse.from(customerRepository.save(customer));

		// Deactivating a customer also deactivates all of their meters.
		if (newStatus == RecordStatus.INACTIVE) {
			deactivateMeters(id);
		}
		return response;
	}

	// Sets every meter of a customer to INACTIVE.
	private void deactivateMeters(Long customerId) {
		var meters = meterRepository.findByCustomerId(customerId);
		meters.forEach(m -> m.setStatus(RecordStatus.INACTIVE));
		meterRepository.saveAll(meters);
	}

	// Delete a customer and all related records (readings, payments, bills, meters, notifications).
	// Order matters because of foreign keys.
	@Transactional
	public void delete(Long id) {
		Customer customer = getEntity(id);
		paymentRepository.deleteByBillCustomerId(id);
		meterReadingRepository.deleteByMeterCustomerId(id);
		billRepository.deleteByCustomerId(id);
		meterRepository.deleteByCustomerId(id);
		notificationRepository.deleteByCustomerId(id);
		customerRepository.delete(customer);
	}
}
