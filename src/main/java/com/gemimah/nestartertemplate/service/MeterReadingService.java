package com.gemimah.nestartertemplate.service;

import com.gemimah.nestartertemplate.dto.MeterReadingRequest;
import com.gemimah.nestartertemplate.dto.MeterReadingResponse;
import com.gemimah.nestartertemplate.dto.PageResponse;
import com.gemimah.nestartertemplate.entity.Meter;
import com.gemimah.nestartertemplate.entity.MeterReading;
import com.gemimah.nestartertemplate.entity.RecordStatus;
import com.gemimah.nestartertemplate.exception.ApiException;
import com.gemimah.nestartertemplate.repository.MeterReadingRepository;
import com.gemimah.nestartertemplate.util.PaginationUtil;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeterReadingService {

	private final MeterService meterService;
	private final MeterReadingRepository meterReadingRepository;

	@Transactional
	public MeterReadingResponse capture(MeterReadingRequest request) {
		Meter meter = meterService.getEntity(request.meterId());
		if (meter.getStatus() != RecordStatus.ACTIVE) {
			throw new ApiException("Meter is inactive", HttpStatus.BAD_REQUEST);
		}

		if (request.currentReading().compareTo(request.previousReading()) <= 0) {
			throw new ApiException("Current reading must be greater than previous reading", HttpStatus.BAD_REQUEST);
		}

		int month = request.readingDate().getMonthValue();
		int year = request.readingDate().getYear();
		if (meterReadingRepository.existsByMeterIdAndReadingMonthAndReadingYear(meter.getId(), month, year)) {
			throw new ApiException("Reading already exists for this meter in the selected month/year", HttpStatus.CONFLICT);
		}

		BigDecimal prev = request.previousReading();
		BigDecimal curr = request.currentReading();
		if (curr.compareTo(prev) <= 0) {
			throw new ApiException("Invalid reading values", HttpStatus.BAD_REQUEST);
		}

		MeterReading reading = MeterReading.builder()
				.meter(meter)
				.previousReading(prev)
				.currentReading(curr)
				.readingDate(request.readingDate())
				.readingMonth(month)
				.readingYear(year)
				.build();

		return MeterReadingResponse.from(meterReadingRepository.save(reading));
	}

	@Transactional(readOnly = true)
	public PageResponse<MeterReadingResponse> getAll(Pageable pageable) {
		return PaginationUtil.map(meterReadingRepository.findAll(pageable), MeterReadingResponse::from);
	}
}
