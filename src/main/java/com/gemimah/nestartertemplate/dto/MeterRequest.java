package com.gemimah.nestartertemplate.dto;

import com.gemimah.nestartertemplate.entity.MeterType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record MeterRequest(
		@NotBlank String meterNumber,
		@NotNull MeterType meterType,
		@NotNull LocalDate installationDate,
		@NotBlank String status,
		@NotNull Long customerId
) {
}
