package com.gemimah.nestartertemplate.repository;

import com.gemimah.nestartertemplate.entity.MeterType;
import com.gemimah.nestartertemplate.entity.Tariff;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TariffRepository extends JpaRepository<Tariff, Long> {

	@Query("""
			select t from Tariff t
			where t.meterType = :meterType
			and (t.effectiveYear < :year or (t.effectiveYear = :year and t.effectiveMonth <= :month))
			order by t.effectiveYear desc, t.effectiveMonth desc, t.version desc
			""")
	java.util.List<Tariff> findApplicable(
			@Param("meterType") MeterType meterType,
			@Param("month") int month,
			@Param("year") int year);

	default Optional<Tariff> findLatestApplicable(MeterType meterType, int month, int year) {
		return findApplicable(meterType, month, year).stream().findFirst();
	}
}
