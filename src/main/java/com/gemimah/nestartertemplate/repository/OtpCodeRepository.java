package com.gemimah.nestartertemplate.repository;

import com.gemimah.nestartertemplate.entity.OtpCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

	Optional<OtpCode> findTopByEmailAndUsedFalseOrderByIdDesc(String email);
}
