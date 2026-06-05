package com.gemimah.nestartertemplate.service;

import com.gemimah.nestartertemplate.dto.AuthResponse;
import com.gemimah.nestartertemplate.dto.LoginRequest;
import com.gemimah.nestartertemplate.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthResponse login(LoginRequest request) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.email(), request.password()));

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String token = jwtService.generateToken(userDetails);
		String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

		return AuthResponse.of(token, userDetails.getUsername(), role);
	}
}
