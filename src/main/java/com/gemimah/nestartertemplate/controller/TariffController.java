package com.gemimah.nestartertemplate.controller;

import com.gemimah.nestartertemplate.dto.TariffRequest;
import com.gemimah.nestartertemplate.dto.TariffResponse;
import com.gemimah.nestartertemplate.service.TariffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tariffs")
@RequiredArgsConstructor
public class TariffController {

	private final TariffService tariffService;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<TariffResponse> create(@Valid @RequestBody TariffRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(tariffService.create(request));
	}
}
