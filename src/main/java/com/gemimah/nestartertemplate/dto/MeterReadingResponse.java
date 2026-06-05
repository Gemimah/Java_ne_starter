package com.gemimah.nestartertemplate.dto;

import com.gemimah.nestartertemplate.entity.MeterReading;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MeterReadingResponse(
		Long id,
		Long meterId,
		BigDecimal previousReading,
		BigDecimal currentReading,
		LocalDate readingDate,
		int readingMonth,
		int readingYear
) {
	public static MeterReadingResponse from(MeterReading reading) {
		return new MeterReadingResponse(
				reading.getId(),
				reading.getMeter().getId(),
				reading.getPreviousReading(),
				reading.getCurrentReading(),
				reading.getReadingDate(),
				reading.getReadingMonth(),
				reading.getReadingYear());
	}
}
