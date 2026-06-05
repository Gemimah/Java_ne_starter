package com.gemimah.nestartertemplate.dto;

import com.gemimah.nestartertemplate.entity.Bill;
import com.gemimah.nestartertemplate.entity.BillStatus;
import com.gemimah.nestartertemplate.entity.MeterType;
import java.math.BigDecimal;
import java.time.LocalDate;

// Read-only view of a bill returned by the API.
public record BillResponse(
		Long id,
		Long customerId,
		MeterType meterType,
		int billingMonth,
		int billingYear,
		BillStatus status,
		BigDecimal consumption,
		BigDecimal baseAmount,
		BigDecimal fixedCharge,
		BigDecimal vatAmount,
		BigDecimal penaltyAmount,
		BigDecimal totalAmount,
		BigDecimal outstandingBalance,
		LocalDate dueDate
) {
	public static BillResponse from(Bill bill) {
		return new BillResponse(
				bill.getId(),
				bill.getCustomer().getId(),
				bill.getMeterType(),
				bill.getBillingMonth(),
				bill.getBillingYear(),
				bill.getStatus(),
				bill.getConsumption(),
				bill.getBaseAmount(),
				bill.getFixedCharge(),
				bill.getVatAmount(),
				bill.getPenaltyAmount(),
				bill.getTotalAmount(),
				bill.getOutstandingBalance(),
				bill.getDueDate());
	}
}
