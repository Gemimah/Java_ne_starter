package com.gemimah.nestartertemplate.repository;

import com.gemimah.nestartertemplate.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// Data access for notifications.
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

	void deleteByCustomerId(Long customerId);
}
