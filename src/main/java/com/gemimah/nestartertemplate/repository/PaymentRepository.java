package com.gemimah.nestartertemplate.repository;

import com.gemimah.nestartertemplate.entity.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// Data access for payments.
public interface PaymentRepository extends JpaRepository<Payment, Long> {

	List<Payment> findByBillIdOrderByPaymentDateDesc(Long billId);

	// All payments for every bill belonging to a customer (payment history).
	List<Payment> findByBillCustomerIdOrderByPaymentDateDesc(Long customerId);

	void deleteByBillCustomerId(Long customerId);
}
