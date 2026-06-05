package com.gemimah.nestartertemplate.service;

import com.gemimah.nestartertemplate.dto.PaymentRequest;
import com.gemimah.nestartertemplate.dto.PaymentResponse;
import com.gemimah.nestartertemplate.entity.Bill;
import com.gemimah.nestartertemplate.entity.BillStatus;
import com.gemimah.nestartertemplate.entity.Payment;
import com.gemimah.nestartertemplate.exception.ApiException;
import com.gemimah.nestartertemplate.repository.BillRepository;
import com.gemimah.nestartertemplate.repository.PaymentRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Records payments, updates outstanding balance and bill status (partial/full).
@Service
@RequiredArgsConstructor
public class PaymentService {

	private final BillingService billingService;
	private final BillRepository billRepository;
	private final PaymentRepository paymentRepository;
	private final NotificationService notificationService;

	@Transactional
	public PaymentResponse record(PaymentRequest request) {
		Bill bill = billingService.getEntity(request.billId());
		// Make sure any late penalty is added before accepting payment.
		bill = billingService.applyPenaltyIfOverdue(bill);

		if (bill.getStatus() == BillStatus.PENDING) {
			throw new ApiException("Bill must be approved before payment", HttpStatus.BAD_REQUEST);
		}
		if (request.amountPaid().compareTo(bill.getOutstandingBalance()) > 0) {
			throw new ApiException("Payment exceeds outstanding balance", HttpStatus.BAD_REQUEST);
		}

		Payment payment = Payment.builder()
				.bill(bill)
				.billReference("BILL-" + bill.getId())
				.amountPaid(request.amountPaid().setScale(2, RoundingMode.HALF_UP))
				.paymentMethod(request.paymentMethod())
				.paymentDate(request.paymentDate())
				.build();
		Payment saved = paymentRepository.save(payment);

		BigDecimal newBalance = bill.getOutstandingBalance().subtract(saved.getAmountPaid())
				.setScale(2, RoundingMode.HALF_UP);
		bill.setOutstandingBalance(newBalance);

		if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
			// Fully paid: update status and notify the customer (Task 6 behaviour).
			bill.setStatus(BillStatus.PAID);
			String monthYear = bill.getBillingMonth() + "/" + bill.getBillingYear();
			String message = "Dear " + bill.getCustomer().getFullNames() + ",\n"
					+ "Your " + monthYear + " utility bill of " + bill.getTotalAmount()
					+ " FRW has been successfully processed.";
			notificationService.notify(bill.getCustomer(), "Payment Received - " + monthYear, message);
		} else {
			bill.setStatus(BillStatus.PARTIALLY_PAID);
		}
		billRepository.save(bill);
		return PaymentResponse.from(saved);
	}

	@Transactional(readOnly = true)
	public List<PaymentResponse> getByBill(Long billId) {
		return paymentRepository.findByBillIdOrderByPaymentDateDesc(billId).stream()
				.map(PaymentResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<PaymentResponse> getByCustomer(Long customerId) {
		return paymentRepository.findByBillCustomerIdOrderByPaymentDateDesc(customerId).stream()
				.map(PaymentResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<PaymentResponse> getAll() {
		return paymentRepository.findAll().stream().map(PaymentResponse::from).toList();
	}
}
