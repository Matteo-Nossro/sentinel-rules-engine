package com.sentinel.rulesengine.infrastructure.persistence;

import com.sentinel.rulesengine.domain.model.Alert;
import com.sentinel.rulesengine.domain.port.out.AlertRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlertRepositoryAdapter implements AlertRepository {

    private final AlertJpaRepository jpaRepository;
    private final AlertMapper mapper;

    public AlertRepositoryAdapter(AlertJpaRepository jpaRepository, AlertMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Alert save(Alert alert) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(alert)));
    }

    @Override
    public List<Alert> findAll() {
        return mapper.toDomainList(jpaRepository.findAll());
    }
}
