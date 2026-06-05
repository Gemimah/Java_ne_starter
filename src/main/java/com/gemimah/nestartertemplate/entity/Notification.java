package com.gemimah.nestartertemplate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// In-app notification for a customer. Also sent out as an email.
// Rows can be created by the application OR by the database trigger (Task 6).
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Customer who should receive this notification.
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	// Short subject line (also used as email subject).
	@Column(nullable = false)
	private String subject;

	// Full message body (also the email body).
	@Column(name = "message", nullable = false, columnDefinition = "TEXT")
	private String message;

	// Whether the customer has read it (toggled via PUT /api/notifications/{id}/read).
	@Column(name = "is_read", nullable = false)
	private boolean readFlag;

	// When the notification was created.
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;
}
