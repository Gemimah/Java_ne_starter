package com.gemimah.nestartertemplate.dto;

import com.gemimah.nestartertemplate.entity.Meter;
import com.gemimah.nestartertemplate.entity.MeterType;
import com.gemimah.nestartertemplate.entity.RecordStatus;
import java.time.LocalDate;

public record MeterResponse(
		Long id,
		String meterNumber,
		MeterType meterType,
		LocalDate installationDate,
		RecordStatus status,
		Long customerId
) {
	public static MeterResponse from(Meter meter) {
		return new MeterResponse(
				meter.getId(),
				meter.getMeterNumber(),
				meter.getMeterType(),
				meter.getInstallationDate(),
				meter.getStatus(),
				meter.getCustomer().getId());
	}
}
