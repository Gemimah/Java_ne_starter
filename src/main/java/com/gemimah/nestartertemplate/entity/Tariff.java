package com.gemimah.nestartertemplate.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tariffs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tariff {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MeterType meterType;

	@Column(nullable = false)
	private int version;

	@Column(nullable = false)
	private int effectiveMonth;

	@Column(nullable = false)
	private int effectiveYear;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TariffType tariffType;

	@Column(precision = 12, scale = 2)
	private BigDecimal flatRate;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal fixedServiceCharge;

	@Column(nullable = false, precision = 5, scale = 2)
	private BigDecimal vatRate;

	@Column(nullable = false, precision = 5, scale = 2)
	private BigDecimal latePenaltyRate;

	@OneToMany(mappedBy = "tariff", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<TariffTier> tiers = new ArrayList<>();
}
