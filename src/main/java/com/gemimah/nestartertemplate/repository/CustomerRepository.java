package com.gemimah.nestartertemplate.repository;

import com.gemimah.nestartertemplate.entity.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	boolean existsByNationalId(String nationalId);

	Optional<Customer> findByEmail(String email);
}
