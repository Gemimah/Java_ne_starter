package com.gemimah.nestartertemplate.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

// Sends plain-text emails via the configured Gmail SMTP account.
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

	private final JavaMailSender mailSender;

	@Value("${app.mail.from}")
	private String from;

	// Sends the OTP verification code.
	public void sendOtp(String to, String code) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setSubject("Your verification code");
		message.setText("Your OTP code is: " + code + "\nIt expires in 10 minutes.");
		mailSender.send(message);
		log.info("OTP email sent to {}", to);
	}

	// Generic notification email. Best-effort: failures are logged, never thrown,
	// so business actions (e.g. bill approval) still succeed if Gmail is unavailable.
	public void sendNotification(String to, String subject, String body) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(from);
			message.setTo(to);
			message.setSubject(subject);
			message.setText(body);
			mailSender.send(message);
			log.info("Notification email sent to {}", to);
		} catch (Exception ex) {
			log.warn("Failed to send notification email to {}: {}", to, ex.getMessage());
		}
	}
}
