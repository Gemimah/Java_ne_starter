package com.gemimah.nestartertemplate.service;

import com.gemimah.nestartertemplate.dto.PageResponse;
import com.gemimah.nestartertemplate.dto.RegisterRequest;
import com.gemimah.nestartertemplate.dto.UserResponse;
import com.gemimah.nestartertemplate.entity.Role;
import com.gemimah.nestartertemplate.entity.User;
import com.gemimah.nestartertemplate.exception.ApiException;
import com.gemimah.nestartertemplate.exception.ResourceNotFoundException;
import com.gemimah.nestartertemplate.repository.UserRepository;
import com.gemimah.nestartertemplate.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public UserResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new ApiException("Email already registered", HttpStatus.CONFLICT);
		}

		Role role = request.role() != null ? request.role() : Role.USER;
		User user = User.builder()
				.email(request.email())
				.password(passwordEncoder.encode(request.password()))
				.role(role)
				.enabled(false)
				.build();

		User saved = userRepository.save(user);
		log.info("Registered user {}", saved.getEmail());
		return UserResponse.from(saved);
	}

	@Transactional(readOnly = true)
	public UserResponse getById(Long id) {
		return userRepository.findById(id)
				.map(UserResponse::from)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
	}

	@Transactional(readOnly = true)
	public PageResponse<UserResponse> getAll(Pageable pageable) {
		return PaginationUtil.map(userRepository.findAll(pageable), UserResponse::from);
	}

	@Transactional
	public void enableUser(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
		user.setEnabled(true);
		userRepository.save(user);
	}
}
