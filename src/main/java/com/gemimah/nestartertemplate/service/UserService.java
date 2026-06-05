package com.gemimah.nestartertemplate.service;

import com.gemimah.nestartertemplate.dto.PageResponse;
import com.gemimah.nestartertemplate.dto.RegisterRequest;
import com.gemimah.nestartertemplate.dto.UserResponse;
import com.gemimah.nestartertemplate.entity.Role;
import com.gemimah.nestartertemplate.entity.User;
import com.gemimah.nestartertemplate.exception.ApiException;
import com.gemimah.nestartertemplate.exception.ResourceNotFoundException;
import com.gemimah.nestartertemplate.repository.UserRepository;
import com.gemimah.nestartertemplate.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// User accounts + signup. Enforces that only admins may create staff roles.
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public UserResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new ApiException("Email already registered", HttpStatus.CONFLICT);
		}

		// Task 1: signup only collects the user attributes. Everyone starts as a
		// CUSTOMER; an ADMIN can promote them later via PUT /api/users/{id}/role.
		boolean enabled = request.status() == null || request.status().equalsIgnoreCase("ACTIVE");
		User user = User.builder()
				.fullNames(request.fullNames())
				.email(request.email())
				.phoneNumber(request.phoneNumber())
				.password(passwordEncoder.encode(request.password()))
				.role(Role.CUSTOMER)
				.enabled(enabled)
				.build();

		User saved = userRepository.save(user);
		log.info("Registered user {} as CUSTOMER", saved.getEmail());
		return UserResponse.from(saved);
	}

	// Soft delete: admin activates/deactivates a user instead of physically
	// deleting it (billing systems keep audit history and data integrity).
	@Transactional
	public UserResponse setStatus(Long id, String status) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
		user.setEnabled(status.equalsIgnoreCase("ACTIVE"));
		log.info("Set user {} status -> {}", user.getEmail(), status.toUpperCase());
		return UserResponse.from(userRepository.save(user));
	}

	// Admin elevates/changes a user's role and (optionally) their active status.
	@Transactional
	public UserResponse updateRole(Long id, Role role, String status) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
		user.setRole(role);
		if (status != null) {
			user.setEnabled(status.equalsIgnoreCase("ACTIVE"));
		}
		log.info("Updated user {} -> role {}", user.getEmail(), role);
		return UserResponse.from(userRepository.save(user));
	}

	@Transactional(readOnly = true)
	public UserResponse getById(Long id) {
		return userRepository.findById(id)
				.map(UserResponse::from)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
	}

	@Transactional(readOnly = true)
	public PageResponse<UserResponse> getAll(Pageable pageable) {
		return PaginationUtil.map(userRepository.findAll(pageable), UserResponse::from);
	}

	@Transactional
	public void enableUser(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
		user.setEnabled(true);
		userRepository.save(user);
	}
}
