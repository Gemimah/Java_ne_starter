package com.gemimah.nestartertemplate.dto;

import com.gemimah.nestartertemplate.entity.Role;
import com.gemimah.nestartertemplate.entity.User;

public record UserResponse(Long id, String email, Role role, boolean enabled) {

	public static UserResponse from(User user) {
		return new UserResponse(user.getId(), user.getEmail(), user.getRole(), user.isEnabled());
	}
}
