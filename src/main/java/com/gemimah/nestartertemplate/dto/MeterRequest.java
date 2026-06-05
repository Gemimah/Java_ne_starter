package com.gemimah.nestartertemplate.dto;

import com.gemimah.nestartertemplate.entity.MeterType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

// Create payload for a meter, always linked to an existing customer.
public record MeterRequest(
		@NotBlank String meterNumber,
		@NotNull MeterType meterType,
		// Installation cannot be in the future.
		@NotNull @PastOrPresent LocalDate installationDate,
		@NotBlank @Pattern(regexp = "(?i)ACTIVE|INACTIVE", message = "Status must be ACTIVE or INACTIVE") String status,
		@NotNull Long customerId
) {
}
