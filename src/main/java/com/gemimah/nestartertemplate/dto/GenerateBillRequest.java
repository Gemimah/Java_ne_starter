package com.gemimah.nestartertemplate.dto;

import com.gemimah.nestartertemplate.entity.MeterType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GenerateBillRequest(
		@NotNull Long customerId,
		@NotNull MeterType meterType,
		@NotNull @Min(1) @Max(12) Integer month,
		@NotNull @Min(2000) Integer year
) {
}
