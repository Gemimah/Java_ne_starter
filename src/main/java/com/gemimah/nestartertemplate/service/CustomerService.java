package com.gemimah.nestartertemplate.service;

import com.gemimah.nestartertemplate.dto.CustomerRequest;
import com.gemimah.nestartertemplate.dto.CustomerResponse;
import com.gemimah.nestartertemplate.dto.PageResponse;
import com.gemimah.nestartertemplate.entity.Customer;
import com.gemimah.nestartertemplate.entity.RecordStatus;
import com.gemimah.nestartertemplate.exception.ApiException;
import com.gemimah.nestartertemplate.exception.ResourceNotFoundException;
import com.gemimah.nestartertemplate.repository.CustomerRepository;
import com.gemimah.nestartertemplate.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository customerRepository;

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
}
