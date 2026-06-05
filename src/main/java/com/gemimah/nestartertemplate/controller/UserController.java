package com.gemimah.nestartertemplate.controller;

import com.gemimah.nestartertemplate.dto.PageResponse;
import com.gemimah.nestartertemplate.dto.UpdateUserRoleRequest;
import com.gemimah.nestartertemplate.dto.UpdateUserStatusRequest;
import com.gemimah.nestartertemplate.dto.UserResponse;
import com.gemimah.nestartertemplate.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "02. Users", description = "User management (ADMIN)")
public class UserController {

	private final UserService userService;

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<PageResponse<UserResponse>> getAll(
			@PageableDefault(size = 10, sort = "id") Pageable pageable) {
		return ResponseEntity.ok(userService.getAll(pageable));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getById(id));
	}

	// Admin elevates/changes a user's role (e.g. CUSTOMER -> OPERATOR/FINANCE/ADMIN).
	@PutMapping("/{id}/role")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserResponse> updateRole(@PathVariable Long id,
			@Valid @RequestBody UpdateUserRoleRequest request) {
		return ResponseEntity.ok(userService.updateRole(id, request.role(), request.status()));
	}

	// Soft delete: admin activates/deactivates a user (no physical deletion).
	@PutMapping("/{id}/status")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserResponse> setStatus(@PathVariable Long id,
			@Valid @RequestBody UpdateUserStatusRequest request) {
		return ResponseEntity.ok(userService.setStatus(id, request.status()));
	}
}
