package com.gemimah.nestartertemplate.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

// Operator payload to capture a reading. Business rules checked in the service.
public record MeterReadingRequest(
		@NotNull Long meterId,
		@NotNull @PositiveOrZero BigDecimal previousReading,
		@NotNull @PositiveOrZero BigDecimal currentReading,
		@NotNull @PastOrPresent LocalDate readingDate
) {
}
