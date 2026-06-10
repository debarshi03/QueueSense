package com.example.QueueSense.QueueSense.repository;

import com.example.QueueSense.QueueSense.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken,Long> {

    boolean existsByToken(String token);
}