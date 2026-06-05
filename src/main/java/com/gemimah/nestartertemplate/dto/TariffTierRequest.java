package com.gemimah.nestartertemplate.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

// One band of a tier-based tariff. tierTo may be null for the last (open-ended) tier.
public record TariffTierRequest(
		@NotNull @PositiveOrZero BigDecimal tierFrom,
		BigDecimal tierTo,
		@NotNull @PositiveOrZero BigDecimal ratePerUnit
) {
}
