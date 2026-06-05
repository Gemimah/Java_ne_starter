package com.gemimah.nestartertemplate.controller;

import com.gemimah.nestartertemplate.dto.TariffRequest;
import com.gemimah.nestartertemplate.dto.TariffResponse;
import com.gemimah.nestartertemplate.entity.MeterType;
import com.gemimah.nestartertemplate.service.TariffService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Task 5: tariff configuration (admin). Tariffs are versioned.
@RestController
@RequestMapping("/api/tariffs")
@RequiredArgsConstructor
@Tag(name = "05. Tariffs", description = "Tariff, tax and penalty configuration (ADMIN)")
public class TariffController {

	private final TariffService tariffService;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<TariffResponse> create(@Valid @RequestBody TariffRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(tariffService.create(request));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'OPERATOR')")
	public ResponseEntity<List<TariffResponse>> getAll() {
		return ResponseEntity.ok(tariffService.getAll());
	}

	@GetMapping("/active/{meterType}")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'OPERATOR')")
	public ResponseEntity<TariffResponse> getActive(@PathVariable MeterType meterType) {
		return ResponseEntity.ok(tariffService.getActive(meterType));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<TariffResponse> update(@PathVariable Long id, @Valid @RequestBody TariffRequest request) {
		return ResponseEntity.ok(tariffService.update(id, request));
	}
}
