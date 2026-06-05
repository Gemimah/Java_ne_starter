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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Captures meter readings with the exam's business rules.
@Service
@RequiredArgsConstructor
public class MeterReadingService {

	private final MeterService meterService;
	private final MeterReadingRepository meterReadingRepository;

	// Rules: meter active, current > previous, one reading per meter per month/year.
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

		MeterReading reading = MeterReading.builder()
				.meter(meter)
				.previousReading(request.previousReading())
				.currentReading(request.currentReading())
				.readingDate(request.readingDate())
				.readingMonth(month)
				.readingYear(year)
				.build();

		return MeterReadingResponse.from(meterReadingRepository.save(reading));
	}

	@Transactional(readOnly = true)
	public MeterReading getEntity(Long id) {
		return meterReadingRepository.findById(id)
				.orElseThrow(() -> new ApiException("Reading not found: " + id, HttpStatus.NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public PageResponse<MeterReadingResponse> getAll(Pageable pageable) {
		return PaginationUtil.map(meterReadingRepository.findAll(pageable), MeterReadingResponse::from);
	}

	@Transactional(readOnly = true)
	public List<MeterReadingResponse> getByMeter(Long meterId) {
		return meterReadingRepository.findByMeterId(meterId).stream().map(MeterReadingResponse::from).toList();
	}
}
