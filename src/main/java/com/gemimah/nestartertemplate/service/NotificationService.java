package com.gemimah.nestartertemplate.service;

import com.gemimah.nestartertemplate.dto.NotificationResponse;
import com.gemimah.nestartertemplate.entity.Customer;
import com.gemimah.nestartertemplate.entity.Notification;
import com.gemimah.nestartertemplate.exception.ResourceNotFoundException;
import com.gemimah.nestartertemplate.mail.EmailService;
import com.gemimah.nestartertemplate.repository.NotificationRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Creates in-app notifications AND sends them as emails (best-effort).
@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final EmailService emailService;

	// Persist a notification for the customer and also email it.
	@Transactional
	public Notification notify(Customer customer, String subject, String message) {
		Notification notification = Notification.builder()
				.customer(customer)
				.subject(subject)
				.message(message)
				.readFlag(false)
				.createdAt(Instant.now())
				.build();
		Notification saved = notificationRepository.save(notification);
		// Every notification is also sent as an email.
		emailService.sendNotification(customer.getEmail(), subject, message);
		return saved;
	}

	@Transactional(readOnly = true)
	public List<NotificationResponse> getAll() {
		return notificationRepository.findAll().stream().map(NotificationResponse::from).toList();
	}

	@Transactional(readOnly = true)
	public List<NotificationResponse> getByCustomer(Long customerId) {
		return notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
				.map(NotificationResponse::from)
				.toList();
	}

	@Transactional
	public NotificationResponse markAsRead(Long id) {
		Notification notification = notificationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
		notification.setReadFlag(true);
		return NotificationResponse.from(notificationRepository.save(notification));
	}
}
