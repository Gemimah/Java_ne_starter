package com.gemimah.nestartertemplate.service;

import com.gemimah.nestartertemplate.dto.MeterRequest;
import com.gemimah.nestartertemplate.dto.MeterResponse;
import com.gemimah.nestartertemplate.dto.PageResponse;
import com.gemimah.nestartertemplate.entity.Meter;
import com.gemimah.nestartertemplate.entity.RecordStatus;
import com.gemimah.nestartertemplate.exception.ApiException;
import com.gemimah.nestartertemplate.exception.ResourceNotFoundException;
import com.gemimah.nestartertemplate.repository.MeterRepository;
import com.gemimah.nestartertemplate.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeterService {

	private final MeterRepository meterRepository;
	private final CustomerService customerService;

	@Transactional
	public MeterResponse create(MeterRequest request) {
		if (meterRepository.existsByMeterNumber(request.meterNumber())) {
			throw new ApiException("Meter number already exists", HttpStatus.CONFLICT);
		}

		Meter meter = Meter.builder()
				.meterNumber(request.meterNumber())
				.meterType(request.meterType())
				.installationDate(request.installationDate())
				.status(RecordStatus.valueOf(request.status().toUpperCase()))
				.customer(customerService.getEntity(request.customerId()))
				.build();
		return MeterResponse.from(meterRepository.save(meter));
	}

	@Transactional(readOnly = true)
	public Meter getEntity(Long id) {
		return meterRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Meter not found: " + id));
	}

	@Transactional(readOnly = true)
	public PageResponse<MeterResponse> getAll(Pageable pageable) {
		return PaginationUtil.map(meterRepository.findAll(pageable), MeterResponse::from);
	}
}
