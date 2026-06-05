package com.gemimah.nestartertemplate.dto;

import com.gemimah.nestartertemplate.entity.Payment;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentResponse(
		Long id,
		Long billId,
		String billReference,
		BigDecimal amountPaid,
		String paymentMethod,
		LocalDate paymentDate
) {
	public static PaymentResponse from(Payment payment) {
		return new PaymentResponse(
				payment.getId(),
				payment.getBill().getId(),
				payment.getBillReference(),
				payment.getAmountPaid(),
				payment.getPaymentMethod(),
				payment.getPaymentDate());
	}
}
