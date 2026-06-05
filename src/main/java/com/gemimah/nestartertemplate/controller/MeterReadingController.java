package com.gemimah.nestartertemplate.controller;

import com.gemimah.nestartertemplate.dto.MeterReadingRequest;
import com.gemimah.nestartertemplate.dto.MeterReadingResponse;
import com.gemimah.nestartertemplate.dto.PageResponse;
import com.gemimah.nestartertemplate.service.MeterReadingService;
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

// Task 6: meter reading capture (operator) + viewing.
@RestController
@RequestMapping("/api/readings")
@RequiredArgsConstructor
@Tag(name = "06. Meter Readings", description = "Capture and view readings (OPERATOR)")
public class MeterReadingController {

	private final MeterReadingService meterReadingService;

	@PostMapping
	@PreAuthorize("hasRole('OPERATOR')")
	public ResponseEntity<MeterReadingResponse> capture(@Valid @RequestBody MeterReadingRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(meterReadingService.capture(request));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'FINANCE')")
	public ResponseEntity<PageResponse<MeterReadingResponse>> getAll(@PageableDefault(size = 20) Pageable pageable) {
		return ResponseEntity.ok(meterReadingService.getAll(pageable));
	}

	@GetMapping("/meter/{meterId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'FINANCE')")
	public ResponseEntity<List<MeterReadingResponse>> getByMeter(@PathVariable Long meterId) {
		return ResponseEntity.ok(meterReadingService.getByMeter(meterId));
	}
}
