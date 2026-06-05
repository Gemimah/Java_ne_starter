package com.gemimah.nestartertemplate.repository;

import com.gemimah.nestartertemplate.entity.MeterReading;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// Data access for meter readings.
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {

	// Enforces "one reading per meter per month/year".
	boolean existsByMeterIdAndReadingMonthAndReadingYear(Long meterId, int readingMonth, int readingYear);

	List<MeterReading> findByMeterId(Long meterId);

	void deleteByMeterCustomerId(Long customerId);
}
