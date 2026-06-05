package com.gemimah.nestartertemplate.controller;

import com.gemimah.nestartertemplate.dto.NotificationResponse;
import com.gemimah.nestartertemplate.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Task 13: notifications (admin views all, customer views own, mark as read).
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "09. Notifications", description = "View notifications and mark as read")
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
	public ResponseEntity<List<NotificationResponse>> getAll() {
		return ResponseEntity.ok(notificationService.getAll());
	}

	@GetMapping("/customer/{customerId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'CUSTOMER')")
	public ResponseEntity<List<NotificationResponse>> getByCustomer(@PathVariable Long customerId) {
		return ResponseEntity.ok(notificationService.getByCustomer(customerId));
	}

	@PutMapping("/{id}/read")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'CUSTOMER')")
	public ResponseEntity<NotificationResponse> markRead(@PathVariable Long id) {
		return ResponseEntity.ok(notificationService.markAsRead(id));
	}
}
