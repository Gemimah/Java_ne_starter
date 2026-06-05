package com.gemimah.nestartertemplate.dto;

import com.gemimah.nestartertemplate.entity.Role;
import com.gemimah.nestartertemplate.entity.User;

public record UserResponse(
		Long id,
		String fullNames,
		String email,
		String phoneNumber,
		Role role,
		String status) {

	public static UserResponse from(User user) {
		String status = user.isEnabled() ? "ACTIVE" : "INACTIVE";
		return new UserResponse(
				user.getId(),
				user.getFullNames(),
				user.getEmail(),
				user.getPhoneNumber(),
				user.getRole(),
				status);
	}
}
