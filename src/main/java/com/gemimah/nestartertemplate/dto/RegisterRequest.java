package com.gemimah.nestartertemplate.dto;

import com.gemimah.nestartertemplate.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank @Email String email,
		@NotBlank @Size(min = 6, max = 100) String password,
		Role role
) {
}
