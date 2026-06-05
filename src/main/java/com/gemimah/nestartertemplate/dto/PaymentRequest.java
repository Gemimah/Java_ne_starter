package com.gemimah.nestartertemplate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentRequest(
		@NotNull Long billId,
		@NotNull BigDecimal amountPaid,
		@NotBlank String paymentMethod,
		@NotNull LocalDate paymentDate
) {
}
