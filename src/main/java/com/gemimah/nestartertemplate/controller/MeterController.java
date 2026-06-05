package com.gemimah.nestartertemplate.controller;

import com.gemimah.nestartertemplate.dto.MeterRequest;
import com.gemimah.nestartertemplate.dto.MeterResponse;
import com.gemimah.nestartertemplate.dto.PageResponse;
import com.gemimah.nestartertemplate.service.MeterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meters")
@RequiredArgsConstructor
public class MeterController {

	private final MeterService meterService;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<MeterResponse> create(@Valid @RequestBody MeterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(meterService.create(request));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'FINANCE')")
	public ResponseEntity<PageResponse<MeterResponse>> getAll(@PageableDefault(size = 20) Pageable pageable) {
		return ResponseEntity.ok(meterService.getAll(pageable));
	}
}
