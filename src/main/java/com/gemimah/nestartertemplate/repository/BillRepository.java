package com.gemimah.nestartertemplate.repository;

import com.gemimah.nestartertemplate.entity.Bill;
import com.gemimah.nestartertemplate.entity.MeterType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// Data access for bills.
public interface BillRepository extends JpaRepository<Bill, Long> {

	// Used to prevent duplicate bills for the same customer/meterType/month/year.
	boolean existsByCustomerIdAndMeterTypeAndBillingMonthAndBillingYear(
			Long customerId, MeterType meterType, int month, int year);

	List<Bill> findByCustomerIdOrderByBillingYearDescBillingMonthDesc(Long customerId);

	void deleteByCustomerId(Long customerId);
}
