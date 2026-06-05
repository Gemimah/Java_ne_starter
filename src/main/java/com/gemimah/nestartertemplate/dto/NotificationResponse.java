package com.gemimah.nestartertemplate.dto;

import com.gemimah.nestartertemplate.entity.Notification;
import java.time.Instant;

// Read-only view of a notification.
public record NotificationResponse(
		Long id,
		Long customerId,
		String subject,
		String message,
		boolean read,
		Instant createdAt
) {
	public static NotificationResponse from(Notification n) {
		return new NotificationResponse(
				n.getId(),
				n.getCustomer().getId(),
				n.getSubject(),
				n.getMessage(),
				n.isReadFlag(),
				n.getCreatedAt());
	}
}
