package com.gemimah.nestartertemplate.dto;

import com.gemimah.nestartertemplate.entity.Role;
import jakarta.validation.constraints.NotNull;

// Admin payload to change a user's role and/or active status.
public record UpdateUserRoleRequest(
		@NotNull Role role,
		// ACTIVE or INACTIVE; null leaves the current status unchanged.
		String status
) {
}
