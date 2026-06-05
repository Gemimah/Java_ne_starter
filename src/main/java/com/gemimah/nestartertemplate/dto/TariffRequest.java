package com.gemimah.nestartertemplate.dto;

import com.gemimah.nestartertemplate.entity.MeterType;
import com.gemimah.nestartertemplate.entity.TariffType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;

// Create/update payload for a tariff version. FLAT uses flatRate; TIERED uses tiers.
public record TariffRequest(
		@NotNull MeterType meterType,
		@NotNull @PositiveOrZero Integer version,
		@NotNull Integer effectiveMonth,
		@NotNull Integer effectiveYear,
		@NotNull TariffType tariffType,
		@PositiveOrZero BigDecimal flatRate,
		@NotNull @PositiveOrZero BigDecimal fixedServiceCharge,
		@NotNull @PositiveOrZero @DecimalMax("100.0") BigDecimal vatRate,
		@NotNull @PositiveOrZero @DecimalMax("100.0") BigDecimal latePenaltyRate,
		List<TariffTierRequest> tiers
) {
}
