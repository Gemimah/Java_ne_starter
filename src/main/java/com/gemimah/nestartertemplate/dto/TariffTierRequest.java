package com.gemimah.nestartertemplate.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TariffTierRequest(
		@NotNull BigDecimal tierFrom,
		BigDecimal tierTo,
		@NotNull BigDecimal ratePerUnit
) {
}
