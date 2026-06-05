package com.gemimah.nestartertemplate.service;

import com.gemimah.nestartertemplate.dto.TariffRequest;
import com.gemimah.nestartertemplate.dto.TariffResponse;
import com.gemimah.nestartertemplate.entity.Tariff;
import com.gemimah.nestartertemplate.entity.TariffTier;
import com.gemimah.nestartertemplate.entity.TariffType;
import com.gemimah.nestartertemplate.exception.ApiException;
import com.gemimah.nestartertemplate.repository.TariffRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TariffService {

	private final TariffRepository tariffRepository;

	@Transactional
	public TariffResponse create(TariffRequest request) {
		Tariff tariff = Tariff.builder()
				.meterType(request.meterType())
				.version(request.version())
				.effectiveMonth(request.effectiveMonth())
				.effectiveYear(request.effectiveYear())
				.tariffType(request.tariffType())
				.flatRate(request.flatRate())
				.fixedServiceCharge(request.fixedServiceCharge())
				.vatRate(request.vatRate())
				.latePenaltyRate(request.latePenaltyRate())
				.build();

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

		return TariffResponse.from(tariffRepository.save(tariff));
	}

	@Transactional(readOnly = true)
	public Tariff getApplicable(com.gemimah.nestartertemplate.entity.MeterType meterType, int month, int year) {
		return tariffRepository.findLatestApplicable(meterType, month, year)
				.orElseThrow(() -> new ApiException("No applicable tariff configured", HttpStatus.BAD_REQUEST));
	}
}
