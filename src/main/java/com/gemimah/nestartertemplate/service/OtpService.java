package com.gemimah.nestartertemplate.service;

import com.gemimah.nestartertemplate.dto.OtpRequest;
import com.gemimah.nestartertemplate.dto.OtpVerifyRequest;
import com.gemimah.nestartertemplate.entity.OtpCode;
import com.gemimah.nestartertemplate.exception.ApiException;
import com.gemimah.nestartertemplate.exception.ResourceNotFoundException;
import com.gemimah.nestartertemplate.mail.EmailService;
import com.gemimah.nestartertemplate.repository.OtpCodeRepository;
import com.gemimah.nestartertemplate.repository.UserRepository;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

	private final OtpCodeRepository otpCodeRepository;
	private final UserRepository userRepository;
	private final EmailService emailService;
	private final UserService userService;

	@Value("${app.otp.length:6}")
	private int otpLength;

	@Value("${app.otp.expiry-minutes:10}")
	private int expiryMinutes;

	private final SecureRandom random = new SecureRandom();

	@Transactional
	public void sendOtp(OtpRequest request) {
		userRepository.findByEmail(request.email())
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.email()));

		String code = generateCode();
		OtpCode otp = OtpCode.builder()
				.email(request.email())
				.code(code)
				.expiresAt(Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES))
				.used(false)
				.build();

		otpCodeRepository.save(otp);
		emailService.sendOtp(request.email(), code);
		log.info("OTP generated for {}", request.email());
	}

	@Transactional
	public void verifyOtp(OtpVerifyRequest request) {
		OtpCode otp = otpCodeRepository.findTopByEmailAndUsedFalseOrderByIdDesc(request.email())
				.orElseThrow(() -> new ApiException("No active OTP found", HttpStatus.BAD_REQUEST));

		if (otp.isUsed() || otp.getExpiresAt().isBefore(Instant.now())) {
			throw new ApiException("OTP expired", HttpStatus.BAD_REQUEST);
		}

		if (!otp.getCode().equals(request.code())) {
			throw new ApiException("Invalid OTP", HttpStatus.BAD_REQUEST);
		}

		otp.setUsed(true);
		otpCodeRepository.save(otp);
		userService.enableUser(request.email());
	}

	private String generateCode() {
		int bound = (int) Math.pow(10, otpLength);
		int code = random.nextInt(bound / 10, bound);
		return String.valueOf(code);
	}
}
