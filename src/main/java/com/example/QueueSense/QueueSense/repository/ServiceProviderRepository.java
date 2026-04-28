package com.example.QueueSense.QueueSense.repository;

import com.example.QueueSense.QueueSense.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

}