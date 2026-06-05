package com.gemimah.nestartertemplate.repository;

import com.gemimah.nestartertemplate.entity.MeterType;
import com.gemimah.nestartertemplate.entity.Tariff;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Data access for tariffs (versioned configuration).
public interface TariffRepository extends JpaRepository<Tariff, Long> {

	// Tariffs for a meter type that are already in effect for the given month/year,
	// newest first. Used to pick the correct version when generating a bill.
	@Query("""
			select t from Tariff t
			where t.meterType = :meterType
			and (t.effectiveYear < :year or (t.effectiveYear = :year and t.effectiveMonth <= :month))
			order by t.effectiveYear desc, t.effectiveMonth desc, t.version desc
			""")
	List<Tariff> findApplicable(
			@Param("meterType") MeterType meterType,
			@Param("month") int month,
			@Param("year") int year);

	List<Tariff> findByMeterTypeOrderByEffectiveYearDescEffectiveMonthDescVersionDesc(MeterType meterType);

	default Optional<Tariff> findLatestApplicable(MeterType meterType, int month, int year) {
		return findApplicable(meterType, month, year).stream().findFirst();
	}
}
