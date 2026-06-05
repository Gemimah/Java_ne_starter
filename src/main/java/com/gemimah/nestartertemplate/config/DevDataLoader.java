package com.gemimah.nestartertemplate.config;

import com.gemimah.nestartertemplate.entity.Role;
import com.gemimah.nestartertemplate.entity.User;
import com.gemimah.nestartertemplate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class DevDataLoader implements CommandLineRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {
		// Seed the default WASAC administrator on first startup.
		if (!userRepository.existsByEmail("admin@wasac.com")) {
			userRepository.save(User.builder()
					.fullNames("System Admin")
					.email("admin@wasac.com")
					.phoneNumber("+250700000000")
					.password(passwordEncoder.encode("admin123"))
					.role(Role.ADMIN)
					.enabled(true)
					.build());
		}
	}
}
