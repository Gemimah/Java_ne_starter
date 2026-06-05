package com.gemimah.nestartertemplate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// Create/update payload for a customer. National ID must be exactly 16 digits.
public record CustomerRequest(
		@NotBlank String fullNames,
		@NotBlank @Pattern(regexp = "^[0-9]{16}$", message = "National ID must be exactly 16 digits") String nationalId,
		@NotBlank @Email String email,
		@NotBlank @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number") String phoneNumber,
		@NotBlank String address,
		@NotBlank @Pattern(regexp = "(?i)ACTIVE|INACTIVE", message = "Status must be ACTIVE or INACTIVE") String status
) {
}
