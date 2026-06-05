package com.gemimah.nestartertemplate.dto;

import com.gemimah.nestartertemplate.entity.Tariff;
import com.gemimah.nestartertemplate.entity.TariffType;
import java.math.BigDecimal;

public record TariffResponse(
		Long id,
		String meterType,
		int version,
		int effectiveMonth,
		int effectiveYear,
		TariffType tariffType,
		BigDecimal flatRate,
		BigDecimal fixedServiceCharge,
		BigDecimal vatRate,
		BigDecimal latePenaltyRate
) {
	public static TariffResponse from(Tariff tariff) {
		return new TariffResponse(
				tariff.getId(),
				tariff.getMeterType().name(),
				tariff.getVersion(),
				tariff.getEffectiveMonth(),
				tariff.getEffectiveYear(),
				tariff.getTariffType(),
				tariff.getFlatRate(),
				tariff.getFixedServiceCharge(),
				tariff.getVatRate(),
				tariff.getLatePenaltyRate());
	}
}
