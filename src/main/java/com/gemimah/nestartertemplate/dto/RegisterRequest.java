package com.gemimah.nestartertemplate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Signup payload (Task 1 attributes only). Every signup becomes a CUSTOMER;
// an ADMIN promotes staff later via PUT /api/users/{id}/role.
public record RegisterRequest(
		@NotBlank String fullNames,
		@NotBlank @Email String email,
		// Phone: optional leading +, then 10-15 digits.
		@NotBlank @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number") String phoneNumber,
		@NotBlank @Size(min = 6, max = 100) String password,
		// ACTIVE or INACTIVE (case-insensitive); defaults to ACTIVE when null.
		String status
) {
}
