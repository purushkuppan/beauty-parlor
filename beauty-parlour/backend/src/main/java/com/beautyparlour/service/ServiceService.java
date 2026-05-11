package com.beautyparlour.service;

import com.beautyparlour.exception.AppException;
import com.beautyparlour.model.dto.ServiceRequest;
import com.beautyparlour.model.dto.ServiceResponse;
import com.beautyparlour.model.enums.ServiceCategory;
import com.beautyparlour.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public List<ServiceResponse> listActive(final String category) {
        // Null-safe switch (JEP 441) — case null handles missing query param cleanly
        return switch (category) {
            case null, "" -> serviceRepository.findByIsActiveTrue()
                    .stream().map(ServiceResponse::from).toList();
            default -> serviceRepository.findByCategoryAndIsActiveTrue(
                            ServiceCategory.valueOf(category.toUpperCase()))
                    .stream().map(ServiceResponse::from).toList();
        };
    }

    @Transactional
    public ServiceResponse create(final ServiceRequest req) {
        final var s = new com.beautyparlour.model.entity.Service();
        applyRequest(s, req);
        return ServiceResponse.from(serviceRepository.save(s));
    }

    @Transactional
    public ServiceResponse update(final UUID id, final ServiceRequest req) {
        final var s = serviceRepository.findById(id)
                .orElseThrow(() -> new AppException.NotFound("Service not found"));
        applyRequest(s, req);
        return ServiceResponse.from(serviceRepository.save(s));
    }

    @Transactional
    public void deactivate(final UUID id) {
        final var s = serviceRepository.findById(id)
                .orElseThrow(() -> new AppException.NotFound("Service not found"));
        s.setActive(false);
        serviceRepository.save(s);
    }

    private void applyRequest(final com.beautyparlour.model.entity.Service s, final ServiceRequest req) {
        s.setName(req.name());
        s.setDescription(req.description());
        s.setCategory(req.category());
        s.setPrice(req.price());
        s.setDurationMins(req.durationMins());
    }
}
