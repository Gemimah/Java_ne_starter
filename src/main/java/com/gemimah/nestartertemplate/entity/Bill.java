package com.gemimah.nestartertemplate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bills", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"customer_id", "meter_type", "billing_month", "billing_year"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@Enumerated(EnumType.STRING)
	@Column(name = "meter_type", nullable = false)
	private MeterType meterType;

	@Column(name = "billing_month", nullable = false)
	private int billingMonth;

	@Column(name = "billing_year", nullable = false)
	private int billingYear;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BillStatus status;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal consumption;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal baseAmount;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal fixedCharge;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal vatAmount;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal penaltyAmount;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal totalAmount;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal outstandingBalance;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tariff_id")
	private Tariff tariff;
}
