package com.gemimah.nestartertemplate.service;

import com.gemimah.nestartertemplate.dto.TariffRequest;
import com.gemimah.nestartertemplate.dto.TariffResponse;
import com.gemimah.nestartertemplate.entity.MeterType;
import com.gemimah.nestartertemplate.entity.Tariff;
import com.gemimah.nestartertemplate.entity.TariffTier;
import com.gemimah.nestartertemplate.entity.TariffType;
import com.gemimah.nestartertemplate.exception.ApiException;
import com.gemimah.nestartertemplate.exception.ResourceNotFoundException;
import com.gemimah.nestartertemplate.repository.TariffRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Tariff configuration. Tariffs are versioned and only apply to billing cycles
// at/after their effective month/year.
@Service
@RequiredArgsConstructor
public class TariffService {

	private final TariffRepository tariffRepository;

	@Transactional
	public TariffResponse create(TariffRequest request) {
		Tariff tariff = buildFrom(new Tariff(), request);
		return TariffResponse.from(tariffRepository.save(tariff));
	}

	@Transactional
	public TariffResponse update(Long id, TariffRequest request) {
		Tariff tariff = tariffRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Tariff not found: " + id));
		tariff.getTiers().clear();
		buildFrom(tariff, request);
		return TariffResponse.from(tariffRepository.save(tariff));
	}

	@Transactional(readOnly = true)
	public List<TariffResponse> getAll() {
		return tariffRepository.findAll().stream().map(TariffResponse::from).toList();
	}

	// The currently active tariff for a meter type (latest effective as of today).
	@Transactional(readOnly = true)
	public TariffResponse getActive(MeterType meterType) {
		LocalDate now = LocalDate.now();
		return TariffResponse.from(getApplicable(meterType, now.getMonthValue(), now.getYear()));
	}

	// Picks the correct tariff version for a billing cycle.
	@Transactional(readOnly = true)
	public Tariff getApplicable(MeterType meterType, int month, int year) {
		return tariffRepository.findLatestApplicable(meterType, month, year)
				.orElseThrow(() -> new ApiException("No applicable tariff configured for " + meterType, HttpStatus.BAD_REQUEST));
	}

	// Shared mapping from request to entity (also rebuilds tiers).
	private Tariff buildFrom(Tariff tariff, TariffRequest request) {
		tariff.setMeterType(request.meterType());
		tariff.setVersion(request.version());
		tariff.setEffectiveMonth(request.effectiveMonth());
		tariff.setEffectiveYear(request.effectiveYear());
		tariff.setTariffType(request.tariffType());
		tariff.setFlatRate(request.flatRate());
		tariff.setFixedServiceCharge(request.fixedServiceCharge());
		tariff.setVatRate(request.vatRate());
		tariff.setLatePenaltyRate(request.latePenaltyRate());

		if (request.tariffType() == TariffType.TIERED && (request.tiers() == null || request.tiers().isEmpty())) {
			throw new ApiException("Tiered tariff requires at least one tier", HttpStatus.BAD_REQUEST);
		}
		if (request.tiers() != null) {
			List<TariffTier> tiers = request.tiers().stream()
					.map(t -> TariffTier.builder()
							.tariff(tariff)
							.tierFrom(t.tierFrom())
							.tierTo(t.tierTo())
							.ratePerUnit(t.ratePerUnit())
							.build())
					.toList();
			tariff.getTiers().addAll(tiers);
		}
		return tariff;
	}
}
