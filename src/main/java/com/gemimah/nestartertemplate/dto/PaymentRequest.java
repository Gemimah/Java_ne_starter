package com.gemimah.nestartertemplate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

// Record a payment against a bill. amountPaid must be positive.
public record PaymentRequest(
		@NotNull Long billId,
		@NotNull @Positive BigDecimal amountPaid,
		@NotBlank String paymentMethod,
		@NotNull @PastOrPresent LocalDate paymentDate
) {
}
