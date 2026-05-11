package com.beautyparlour.service;

import com.beautyparlour.exception.AppException;
import com.beautyparlour.model.dto.ServiceRequest;
import com.beautyparlour.model.dto.ServiceResponse;
import com.beautyparlour.model.enums.ServiceCategory;
import com.beautyparlour.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public List<ServiceResponse> listActive(String category) {
        if (category != null && !category.isBlank()) {
            ServiceCategory cat = ServiceCategory.valueOf(category.toUpperCase());
            return serviceRepository.findByCategoryAndIsActiveTrue(cat)
                    .stream().map(ServiceResponse::from).toList();
        }
        return serviceRepository.findByIsActiveTrue()
                .stream().map(ServiceResponse::from).toList();
    }

    @Transactional
    public ServiceResponse create(ServiceRequest req) {
        com.beautyparlour.model.entity.Service s = new com.beautyparlour.model.entity.Service();
        applyRequest(s, req);
        return ServiceResponse.from(serviceRepository.save(s));
    }

    @Transactional
    public ServiceResponse update(UUID id, ServiceRequest req) {
        com.beautyparlour.model.entity.Service s = serviceRepository.findById(id)
                .orElseThrow(() -> new AppException("Service not found", HttpStatus.NOT_FOUND));
        applyRequest(s, req);
        return ServiceResponse.from(serviceRepository.save(s));
    }

    @Transactional
    public void deactivate(UUID id) {
        com.beautyparlour.model.entity.Service s = serviceRepository.findById(id)
                .orElseThrow(() -> new AppException("Service not found", HttpStatus.NOT_FOUND));
        s.setActive(false);
        serviceRepository.save(s);
    }

    private void applyRequest(com.beautyparlour.model.entity.Service s, ServiceRequest req) {
        s.setName(req.name());
        s.setDescription(req.description());
        s.setCategory(req.category());
        s.setPrice(req.price());
        s.setDurationMins(req.durationMins());
    }
}
