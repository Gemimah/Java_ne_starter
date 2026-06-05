package com.gemimah.nestartertemplate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
		@NotBlank String fullNames,
		@NotBlank String nationalId,
		@NotBlank @Email String email,
		@NotBlank String phoneNumber,
		@NotBlank String address,
		@NotBlank String status
) {
}
