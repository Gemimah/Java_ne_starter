package com.gemimah.nestartertemplate.service;

import com.gemimah.nestartertemplate.dto.BillResponse;
import com.gemimah.nestartertemplate.dto.GenerateBillRequest;
import com.gemimah.nestartertemplate.entity.Bill;
import com.gemimah.nestartertemplate.entity.BillStatus;
import com.gemimah.nestartertemplate.entity.Customer;
import com.gemimah.nestartertemplate.entity.Meter;
import com.gemimah.nestartertemplate.entity.MeterReading;
import com.gemimah.nestartertemplate.entity.MeterType;
import com.gemimah.nestartertemplate.entity.RecordStatus;
import com.gemimah.nestartertemplate.entity.Tariff;
import com.gemimah.nestartertemplate.entity.TariffTier;
import com.gemimah.nestartertemplate.entity.TariffType;
import com.gemimah.nestartertemplate.exception.ApiException;
import com.gemimah.nestartertemplate.exception.ResourceNotFoundException;
import com.gemimah.nestartertemplate.repository.BillRepository;
import com.gemimah.nestartertemplate.repository.MeterReadingRepository;
import com.gemimah.nestartertemplate.repository.MeterRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BillingService {

	private final CustomerService customerService;
	private final MeterRepository meterRepository;
	private final MeterReadingRepository meterReadingRepository;
	private final TariffService tariffService;
	private final BillRepository billRepository;

	@Transactional
	public BillResponse generate(GenerateBillRequest request) {
		Customer customer = customerService.getEntity(request.customerId());
		if (customer.getStatus() != RecordStatus.ACTIVE) {
			throw new ApiException("Inactive customers cannot receive bills", HttpStatus.BAD_REQUEST);
		}

		if (billRepository.existsByCustomerIdAndMeterTypeAndBillingMonthAndBillingYear(
				customer.getId(), request.meterType(), request.month(), request.year())) {
			throw new ApiException("Bill already exists for this customer and month/year", HttpStatus.CONFLICT);
		}

		Tariff tariff = tariffService.getApplicable(request.meterType(), request.month(), request.year());
		BigDecimal consumption = calculateConsumption(customer.getId(), request.meterType(), request.month(), request.year());
		BigDecimal baseAmount = calculateBaseAmount(consumption, tariff);
		BigDecimal fixedCharge = tariff.getFixedServiceCharge();
		BigDecimal vat = percentage(baseAmount.add(fixedCharge), tariff.getVatRate());
		BigDecimal penalty = BigDecimal.ZERO;
		BigDecimal total = baseAmount.add(fixedCharge).add(vat).add(penalty).setScale(2, RoundingMode.HALF_UP);

		Bill bill = Bill.builder()
				.customer(customer)
				.meterType(request.meterType())
				.billingMonth(request.month())
				.billingYear(request.year())
				.status(BillStatus.OPEN)
				.consumption(consumption)
				.baseAmount(baseAmount)
				.fixedCharge(fixedCharge)
				.vatAmount(vat)
				.penaltyAmount(penalty)
				.totalAmount(total)
				.outstandingBalance(total)
				.tariff(tariff)
				.build();
		return BillResponse.from(billRepository.save(bill));
	}

	@Transactional(readOnly = true)
	public Bill getEntity(Long billId) {
		return billRepository.findById(billId)
				.orElseThrow(() -> new ResourceNotFoundException("Bill not found: " + billId));
	}

	@Transactional(readOnly = true)
	public List<BillResponse> getCustomerBills(Long customerId) {
		return billRepository.findByCustomerIdOrderByBillingYearDescBillingMonthDesc(customerId)
				.stream()
				.map(BillResponse::from)
				.toList();
	}

	private BigDecimal calculateConsumption(Long customerId, MeterType meterType, int month, int year) {
		List<Meter> meters = meterRepository.findByCustomerIdAndMeterType(customerId, meterType);
		BigDecimal total = BigDecimal.ZERO;
		for (Meter meter : meters) {
			List<MeterReading> readings = meterReadingRepository.findByMeterId(meter.getId()).stream()
					.filter(r -> r.getReadingMonth() == month && r.getReadingYear() == year)
					.sorted(Comparator.comparing(MeterReading::getReadingDate))
					.toList();
			if (!readings.isEmpty()) {
				MeterReading reading = readings.get(readings.size() - 1);
				total = total.add(reading.getCurrentReading().subtract(reading.getPreviousReading()));
			}
		}
		return total.setScale(2, RoundingMode.HALF_UP);
	}

	private BigDecimal calculateBaseAmount(BigDecimal consumption, Tariff tariff) {
		if (tariff.getTariffType() == TariffType.FLAT) {
			BigDecimal rate = tariff.getFlatRate() == null ? BigDecimal.ZERO : tariff.getFlatRate();
			return consumption.multiply(rate).setScale(2, RoundingMode.HALF_UP);
		}

		BigDecimal remaining = consumption;
		BigDecimal amount = BigDecimal.ZERO;
		List<TariffTier> tiers = tariff.getTiers().stream()
				.sorted(Comparator.comparing(TariffTier::getTierFrom))
				.toList();
		for (TariffTier tier : tiers) {
			if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
			BigDecimal tierRange = tier.getTierTo() == null
					? remaining
					: tier.getTierTo().subtract(tier.getTierFrom()).add(BigDecimal.ONE);
			BigDecimal applied = remaining.min(tierRange.max(BigDecimal.ZERO));
			amount = amount.add(applied.multiply(tier.getRatePerUnit()));
			remaining = remaining.subtract(applied);
		}
		return amount.setScale(2, RoundingMode.HALF_UP);
	}

	private BigDecimal percentage(BigDecimal amount, BigDecimal rate) {
		return amount.multiply(rate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
	}
}
