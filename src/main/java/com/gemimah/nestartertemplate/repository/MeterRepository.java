package com.gemimah.nestartertemplate.repository;

import com.gemimah.nestartertemplate.entity.Meter;
import com.gemimah.nestartertemplate.entity.MeterType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeterRepository extends JpaRepository<Meter, Long> {

	boolean existsByMeterNumber(String meterNumber);

	List<Meter> findByCustomerId(Long customerId);

	List<Meter> findByCustomerIdAndMeterType(Long customerId, MeterType meterType);
}
