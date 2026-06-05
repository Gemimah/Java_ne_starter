package com.gemimah.nestartertemplate.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MeterReadingRequest(
		@NotNull Long meterId,
		@NotNull BigDecimal previousReading,
		@NotNull BigDecimal currentReading,
		@NotNull LocalDate readingDate
) {
}
