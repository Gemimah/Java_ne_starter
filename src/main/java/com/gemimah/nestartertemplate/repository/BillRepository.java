package com.gemimah.nestartertemplate.repository;

import com.gemimah.nestartertemplate.entity.Bill;
import com.gemimah.nestartertemplate.entity.MeterType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Long> {

	boolean existsByCustomerIdAndMeterTypeAndBillingMonthAndBillingYear(
			Long customerId, MeterType meterType, int month, int year);

	List<Bill> findByCustomerIdOrderByBillingYearDescBillingMonthDesc(Long customerId);
}
