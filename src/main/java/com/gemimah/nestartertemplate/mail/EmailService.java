package com.gemimah.nestartertemplate.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

	private final JavaMailSender mailSender;

	@Value("${app.mail.from}")
	private String from;

	public void sendOtp(String to, String code) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setSubject("Your verification code");
		message.setText("Your OTP code is: " + code + "\nIt expires in 10 minutes.");
		mailSender.send(message);
		log.info("OTP email sent to {}", to);
	}
}
