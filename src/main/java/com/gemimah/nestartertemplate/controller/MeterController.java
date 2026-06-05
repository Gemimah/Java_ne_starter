package com.gemimah.nestartertemplate.controller;

import com.gemimah.nestartertemplate.dto.MeterRequest;
import com.gemimah.nestartertemplate.dto.MeterResponse;
import com.gemimah.nestartertemplate.dto.PageResponse;
import com.gemimah.nestartertemplate.service.MeterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Task 4: meter setup (admin creates, staff can view).
@RestController
@RequestMapping("/api/meters")
@RequiredArgsConstructor
@Tag(name = "04. Meters", description = "Meter setup (ADMIN creates, staff view)")
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

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'FINANCE')")
	public ResponseEntity<MeterResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(meterService.getById(id));
	}

	@GetMapping("/customer/{customerId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'FINANCE')")
	public ResponseEntity<List<MeterResponse>> getByCustomer(@PathVariable Long customerId) {
		return ResponseEntity.ok(meterService.getByCustomer(customerId));
	}
}
