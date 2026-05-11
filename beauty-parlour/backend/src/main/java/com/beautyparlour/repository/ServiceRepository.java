package com.beautyparlour.repository;

import com.beautyparlour.model.entity.Service;
import com.beautyparlour.model.enums.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceRepository extends JpaRepository<Service, UUID> {
    List<Service> findByIsActiveTrue();
    List<Service> findByCategoryAndIsActiveTrue(ServiceCategory category);
}
