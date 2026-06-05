package com.gemimah.nestartertemplate.dto;

import com.gemimah.nestartertemplate.entity.Customer;
import com.gemimah.nestartertemplate.entity.RecordStatus;

public record CustomerResponse(
		Long id,
		String fullNames,
		String nationalId,
		String email,
		String phoneNumber,
		String address,
		RecordStatus status
) {
	public static CustomerResponse from(Customer customer) {
		return new CustomerResponse(
				customer.getId(),
				customer.getFullNames(),
				customer.getNationalId(),
				customer.getEmail(),
				customer.getPhoneNumber(),
				customer.getAddress(),
				customer.getStatus());
	}
}
