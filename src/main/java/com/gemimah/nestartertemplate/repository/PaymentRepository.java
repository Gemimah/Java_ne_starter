package com.gemimah.nestartertemplate.repository;

import com.gemimah.nestartertemplate.entity.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	List<Payment> findByBillIdOrderByPaymentDateDesc(Long billId);
}
