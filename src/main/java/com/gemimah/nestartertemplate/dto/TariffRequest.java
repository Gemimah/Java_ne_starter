package com.gemimah.nestartertemplate.dto;

import com.gemimah.nestartertemplate.entity.MeterType;
import com.gemimah.nestartertemplate.entity.TariffType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record TariffRequest(
		@NotNull MeterType meterType,
		@NotNull Integer version,
		@NotNull Integer effectiveMonth,
		@NotNull Integer effectiveYear,
		@NotNull TariffType tariffType,
		BigDecimal flatRate,
		@NotNull BigDecimal fixedServiceCharge,
		@NotNull BigDecimal vatRate,
		@NotNull BigDecimal latePenaltyRate,
		List<TariffTierRequest> tiers
) {
}
