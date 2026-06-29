package com.sentinel.rulesengine.infrastructure.persistence;

import com.sentinel.rulesengine.infrastructure.persistence.entity.RuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RuleJpaRepository extends JpaRepository<RuleEntity, UUID> {
    List<RuleEntity> findByActiveTrueAndSourceIdAndMetricName(UUID sourceId, String metricName);
}
