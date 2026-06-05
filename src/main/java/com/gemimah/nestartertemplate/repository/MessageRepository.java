package com.gemimah.nestartertemplate.repository;

import com.gemimah.nestartertemplate.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
