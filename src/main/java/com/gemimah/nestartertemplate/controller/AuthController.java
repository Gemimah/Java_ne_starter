package com.gemimah.nestartertemplate.controller;

import com.gemimah.nestartertemplate.dto.AuthResponse;
import com.gemimah.nestartertemplate.dto.LoginRequest;
import com.gemimah.nestartertemplate.dto.OtpRequest;
import com.gemimah.nestartertemplate.dto.OtpVerifyRequest;
import com.gemimah.nestartertemplate.dto.RegisterRequest;
import com.gemimah.nestartertemplate.dto.UserResponse;
import com.gemimah.nestartertemplate.service.AuthService;
import com.gemimah.nestartertemplate.service.OtpService;
import com.gemimah.nestartertemplate.service.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;
	private final AuthService authService;
	private final OtpService otpService;

	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@PostMapping("/otp/send")
	public ResponseEntity<Map<String, String>> sendOtp(@Valid @RequestBody OtpRequest request) {
		otpService.sendOtp(request);
		return ResponseEntity.ok(Map.of("message", "OTP sent to email"));
	}

	@PostMapping("/otp/verify")
	public ResponseEntity<Map<String, String>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
		otpService.verifyOtp(request);
		return ResponseEntity.ok(Map.of("message", "Account verified successfully"));
	}
}
