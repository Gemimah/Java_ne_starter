package com.gemimah.nestartertemplate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "meter_readings", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"meter_id", "reading_month", "reading_year"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterReading {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "meter_id", nullable = false)
	private Meter meter;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal previousReading;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal currentReading;

	@Column(nullable = false)
	private LocalDate readingDate;

	@Column(nullable = false)
	private int readingMonth;

	@Column(nullable = false)
	private int readingYear;
}
