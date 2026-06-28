package com.sentinel.rulesengine.domain.port.out;

import com.sentinel.rulesengine.domain.model.Rule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RuleRepository {
    Rule save(Rule rule);
    Optional<Rule> findById(UUID id);
    List<Rule> findAll();
    List<Rule> findActiveBySourceAndMetric(UUID sourceId, String metricName);
    void deleteById(UUID id);
}
