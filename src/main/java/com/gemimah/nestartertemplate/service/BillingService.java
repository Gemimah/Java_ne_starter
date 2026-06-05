package com.gemimah.nestartertemplate.service;

import com.gemimah.nestartertemplate.dto.BillResponse;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Bill lifecycle: generate from a reading, approve (notify), apply late penalty.
@Service
@RequiredArgsConstructor
public class BillingService {

	private final MeterReadingService meterReadingService;
	private final TariffService tariffService;
	private final BillRepository billRepository;
	private final NotificationService notificationService;

	// Task 7: generate a bill directly from a captured reading.
	@Transactional
	public BillResponse generateFromReading(Long readingId) {
		MeterReading reading = meterReadingService.getEntity(readingId);
		Meter meter = reading.getMeter();
		Customer customer = meter.getCustomer();

		if (customer.getStatus() != RecordStatus.ACTIVE) {
			throw new ApiException("Inactive customers cannot receive bills", HttpStatus.BAD_REQUEST);
		}

		MeterType meterType = meter.getMeterType();
		int month = reading.getReadingMonth();
		int year = reading.getReadingYear();

		if (billRepository.existsByCustomerIdAndMeterTypeAndBillingMonthAndBillingYear(
				customer.getId(), meterType, month, year)) {
			throw new ApiException("Bill already exists for this customer and month/year", HttpStatus.CONFLICT);
		}

		Tariff tariff = tariffService.getApplicable(meterType, month, year);
		BigDecimal consumption = reading.getCurrentReading().subtract(reading.getPreviousReading())
				.setScale(2, RoundingMode.HALF_UP);
		BigDecimal baseAmount = calculateBaseAmount(consumption, tariff);
		BigDecimal fixedCharge = tariff.getFixedServiceCharge();
		BigDecimal vat = percentage(baseAmount.add(fixedCharge), tariff.getVatRate());
		BigDecimal total = baseAmount.add(fixedCharge).add(vat).setScale(2, RoundingMode.HALF_UP);

		Bill bill = Bill.builder()
				.customer(customer)
				.meterType(meterType)
				.billingMonth(month)
				.billingYear(year)
				.status(BillStatus.PENDING)
				.consumption(consumption)
				.baseAmount(baseAmount)
				.fixedCharge(fixedCharge)
				.vatAmount(vat)
				.penaltyAmount(BigDecimal.ZERO)
				.totalAmount(total)
				.outstandingBalance(total)
				.dueDate(reading.getReadingDate().plusDays(30))
				.penaltyApplied(false)
				.tariff(tariff)
				.build();
		Bill saved = billRepository.save(bill);

		// Task 6: on bill generation, insert a notification message (and email it).
		String monthYear = saved.getBillingMonth() + "/" + saved.getBillingYear();
		String message = "Dear " + customer.getFullNames() + ",\n"
				+ "Your " + monthYear + " utility bill of " + saved.getTotalAmount()
				+ " FRW has been generated and is pending approval.";
		notificationService.notify(customer, "Bill Generated - " + monthYear, message);

		return BillResponse.from(saved);
	}

	// Task 8: finance/admin approves a pending bill; customer is notified + emailed.
	@Transactional
	public BillResponse approve(Long billId) {
		Bill bill = getEntity(billId);
		if (bill.getStatus() != BillStatus.PENDING) {
			throw new ApiException("Only PENDING bills can be approved", HttpStatus.BAD_REQUEST);
		}
		bill.setStatus(BillStatus.APPROVED);
		Bill saved = billRepository.save(bill);

		String monthYear = bill.getBillingMonth() + "/" + bill.getBillingYear();
		String message = "Dear " + bill.getCustomer().getFullNames() + ",\n"
				+ "Your " + monthYear + " utility bill of " + bill.getTotalAmount()
				+ " FRW has been successfully processed.";
		notificationService.notify(bill.getCustomer(), "Bill Approved - " + monthYear, message);
		return BillResponse.from(saved);
	}

	@Transactional(readOnly = true)
	public Bill getEntity(Long billId) {
		return billRepository.findById(billId)
				.orElseThrow(() -> new ResourceNotFoundException("Bill not found: " + billId));
	}

	// Read one bill, applying any overdue penalty first.
	@Transactional
	public BillResponse getById(Long billId) {
		Bill bill = applyPenaltyIfOverdue(getEntity(billId));
		return BillResponse.from(bill);
	}

	// Customer's bills, applying penalties where due.
	@Transactional
	public List<BillResponse> getCustomerBills(Long customerId) {
		return billRepository.findByCustomerIdOrderByBillingYearDescBillingMonthDesc(customerId)
				.stream()
				.map(this::applyPenaltyIfOverdue)
				.map(BillResponse::from)
				.toList();
	}

	// Task 11: charge the late penalty once when an approved/unpaid bill passes its due date.
	@Transactional
	public Bill applyPenaltyIfOverdue(Bill bill) {
		boolean unpaid = bill.getOutstandingBalance().compareTo(BigDecimal.ZERO) > 0;
		boolean billable = bill.getStatus() == BillStatus.APPROVED
				|| bill.getStatus() == BillStatus.PARTIALLY_PAID
				|| bill.getStatus() == BillStatus.OVERDUE;
		boolean pastDue = bill.getDueDate() != null && bill.getDueDate().isBefore(LocalDate.now());

		if (unpaid && billable && pastDue && !bill.isPenaltyApplied() && bill.getTariff() != null) {
			BigDecimal penalty = percentage(bill.getTotalAmount(), bill.getTariff().getLatePenaltyRate());
			bill.setPenaltyAmount(bill.getPenaltyAmount().add(penalty));
			bill.setTotalAmount(bill.getTotalAmount().add(penalty));
			bill.setOutstandingBalance(bill.getOutstandingBalance().add(penalty));
			bill.setPenaltyApplied(true);
			bill.setStatus(BillStatus.OVERDUE);
			return billRepository.save(bill);
		}
		return bill;
	}

	// FLAT: consumption * flatRate. TIERED: sum across configured tiers.
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

	// amount * rate%.
	private BigDecimal percentage(BigDecimal amount, BigDecimal rate) {
		return amount.multiply(rate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
	}
}
