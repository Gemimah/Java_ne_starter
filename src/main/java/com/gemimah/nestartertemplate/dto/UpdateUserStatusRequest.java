package com.gemimah.nestartertemplate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// Admin payload to activate/deactivate a user (soft delete).
public record UpdateUserStatusRequest(
		@NotBlank @Pattern(regexp = "(?i)ACTIVE|INACTIVE", message = "Status must be ACTIVE or INACTIVE") String status
) {
}
